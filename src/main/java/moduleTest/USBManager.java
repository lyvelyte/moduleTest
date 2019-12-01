package moduleTest;

import org.usb4java.*;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class USBManager {

    List<UsbDeviceDescriptor> usbDeviceDescriptorList = new ArrayList<>();

    public USBManager() throws Exception {
        loadUsbDatabase();
    }

    public static short hexStringToShort(String hexNumber) {
        if (hexNumber.length() != 4) {
            return -1;
        }
        int decimal = Integer.parseInt(hexNumber, 16);
        return (short) decimal;
    }

    private static String loadResource(String fileName) throws Exception {
        String result;

        System.out.println("Loading fileName = " + fileName);
        URL url = ClassLoader.getSystemResource(fileName);
        if (url == null) {
            System.out.println("URL is null.");
            throw new Exception();
        }
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
        if (inputStream == null) {
            System.out.println("inputStream is null.");
            throw new Exception();
        }
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        result = scanner.useDelimiter("\\A").next();
        scanner.close();
        inputStream.close();
        System.out.println("Resource loaded.");

        return result;
    }

    public void listDevices() {
        // Create the libusb context
        Context context = new Context();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Read the USB device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and list them
            for (Device device : list) {
                int address = LibUsb.getDeviceAddress(device);
                int busNumber = LibUsb.getBusNumber(device);
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result < 0) {
                    throw new LibUsbException(
                            "Unable to read device descriptor", result);
                }
                System.out.format(
                        "\nBus %03d, Device %03d: Vendor %04x, Product %04x%n",
                        busNumber, address, descriptor.idVendor(),
                        descriptor.idProduct());
                UsbDeviceDescriptor usbDeviceDescriptor = findUSBDevice(descriptor.idVendor(), descriptor.idProduct());
                if (usbDeviceDescriptor != null) {
                    System.out.println("Vendor = " + usbDeviceDescriptor.vendorDescription + ", Product = " + usbDeviceDescriptor.productDescription);
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Deinitialize the libusb context
        LibUsb.exit(context);
    }

    private UsbDeviceDescriptor findUSBDevice(short vendorId, short productId) {
        boolean found = false;
        int i = 0;
        UsbDeviceDescriptor usbDeviceDescriptor = null;
        while (!found && (i < usbDeviceDescriptorList.size())) {
            if ((vendorId == usbDeviceDescriptorList.get(i).vendorId) && (productId == usbDeviceDescriptorList.get(i).productId)) {
                usbDeviceDescriptor = usbDeviceDescriptorList.get(i);
                found = true;
            }
            i++;
        }

        // If no perfect match, try again to at least get the vendor.
        i = 0;
        while (!found && (i < usbDeviceDescriptorList.size())) {
            if ((vendorId == usbDeviceDescriptorList.get(i).vendorId)) {
                usbDeviceDescriptor = new UsbDeviceDescriptor(usbDeviceDescriptorList.get(i).vendorId, usbDeviceDescriptorList.get(i).productId, usbDeviceDescriptorList.get(i).vendorDescription, "");
                found = true;
            }
            i++;
        }

        return usbDeviceDescriptor;
    }

    private void loadUsbDatabase() throws Exception {
        String rawDatabseStr = loadResource("usb.ids");
        String[] lines = rawDatabseStr.split("\n");
        short vendorId = -1;
        short productId;
        String vendorDescription = "";
        String productDescription = "";
        for (String line : lines) {
            if (line.equals("# List of known device classes, subclasses and protocols")) {
                break;
            }
            boolean isCommentOrEmpty = line.isEmpty();
            if (!isCommentOrEmpty) {
                isCommentOrEmpty = line.substring(0, 1).equals("#");
            }
            if (!isCommentOrEmpty) {
                boolean hasTab = line.substring(0, 1).equals("\t");
                if (!hasTab) {
                    String[] smallerLines = line.split("  ");
                    vendorId = hexStringToShort(smallerLines[0]);
                    vendorDescription = smallerLines[1];
                    UsbDeviceDescriptor usbDeviceDescriptor = new UsbDeviceDescriptor(vendorId, (short) -1, vendorDescription, "");
                    usbDeviceDescriptorList.add(usbDeviceDescriptor);
                } else {
                    String[] smallerLines = line.substring(1).split("  ");
                    productId = hexStringToShort(smallerLines[0]);
                    productDescription = smallerLines[1];
                    UsbDeviceDescriptor usbDeviceDescriptor = new UsbDeviceDescriptor(vendorId, productId, vendorDescription, productDescription);
                    usbDeviceDescriptorList.add(usbDeviceDescriptor);
                }
            }
        }
    }
}
