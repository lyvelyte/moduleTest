package moduleTest;

import org.usb4java.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class USBManager {
    /**
     * The USB communication timeout.
     */
    private static final int TIMEOUT = 1000;
    public boolean isConnected = false;
    int result;
    DeviceHandle handle;
    int attached;
    List<UsbDeviceInfo> usbDeviceInfoList = new ArrayList<>();

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

    public static void sendMessage(DeviceHandle handle, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.rewind();
        int transfered = LibUsb.controlTransfer(handle,
                (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
                (byte) 0x09, (short) 2, (short) 1, buffer, TIMEOUT);
        if (transfered < 0)
            throw new LibUsbException("Control transfer failed", transfered);
        if (transfered != data.length)
            throw new RuntimeException("Not all data was sent to device");
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

    public void connect(short vendorId, short productId) {
        // Initialize the libusb context
        result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Search for the missile launcher USB device and stop when not found
        Device device = findDevice(vendorId, productId);
        if (device == null) {
            System.err.println("Missile launcher not found.");
            System.exit(1);
        }

        // Open the device
        DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to open USB device", result);
        }
        try {
            // Check if kernel driver is attached to the interface
            attached = LibUsb.kernelDriverActive(handle, 1);
            if (attached < 0) {
                throw new LibUsbException(
                        "Unable to check kernel driver active", result);
            }

            // Detach kernel driver from interface 0 and 1. This can fail if
            // kernel is not attached to the device or operating system
            // doesn't support this operation. These cases are ignored here.
            result = LibUsb.detachKernelDriver(handle, 1);
            if (result != LibUsb.SUCCESS &&
                    result != LibUsb.ERROR_NOT_SUPPORTED &&
                    result != LibUsb.ERROR_NOT_FOUND) {
                throw new LibUsbException("Unable to detach kernel driver",
                        result);
            }

            // Claim interface
            result = LibUsb.claimInterface(handle, 1);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }

            isConnected = true;
        } catch (Exception ignored) {

        }
    }

    public void disconnect() {
        // Release the interface
        result = LibUsb.releaseInterface(handle, 1);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to release interface",
                    result);
        }

        // Re-attach kernel driver if needed
        if (attached == 1) {
            LibUsb.attachKernelDriver(handle, 1);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException(
                        "Unable to re-attach kernel driver", result);
            }
        }

        isConnected = false;
    }

    public List<UsbDeviceInfo> listDevices() {
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

        List<UsbDeviceInfo> attachedDevicesInfo = new ArrayList<>();

        System.out.println();

        try {
            // Iterate over all devices and list them
            for (Device device : list) {
                int address = LibUsb.getDeviceAddress(device);
                int busNumber = LibUsb.getBusNumber(device);
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result < 0) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
//                System.out.format("\nBus %03d, Device %03d: Vendor %04x, Product %04x%n", busNumber, address, descriptor.idVendor(), descriptor.idProduct());
                UsbDeviceInfo usbDeviceInfo = findUSBDevice(descriptor.idVendor(), descriptor.idProduct(), descriptor, device);
                if (usbDeviceInfo != null) {
                    attachedDevicesInfo.add(usbDeviceInfo);
//                    System.out.println("Vendor = " + usbDeviceInfo.vendorDescription + ", Product = " + usbDeviceInfo.productDescription);
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Deinitialize the libusb context
        LibUsb.exit(context);
        return attachedDevicesInfo;
    }

    private UsbDeviceInfo findUSBDevice(short vendorId, short productId, DeviceDescriptor descriptor, Device device) {
        boolean found = false;
        int i = 0;
        UsbDeviceInfo usbDeviceInfo = null;
        while (!found && (i < usbDeviceInfoList.size())) {
            if ((vendorId == usbDeviceInfoList.get(i).vendorId) && (productId == usbDeviceInfoList.get(i).productId)) {
                usbDeviceInfo = usbDeviceInfoList.get(i);
                usbDeviceInfo.descriptor = descriptor;
                usbDeviceInfo.device = device;
                found = true;
            }
            i++;
        }

        // If no perfect match, try again to at least get the vendor.
        i = 0;
        while (!found && (i < usbDeviceInfoList.size())) {
            if ((vendorId == usbDeviceInfoList.get(i).vendorId)) {
                usbDeviceInfo = new UsbDeviceInfo(usbDeviceInfoList.get(i).vendorId, usbDeviceInfoList.get(i).productId, usbDeviceInfoList.get(i).vendorDescription, "");
                usbDeviceInfo.descriptor = descriptor;
                usbDeviceInfo.device = device;
                found = true;
            }
            i++;
        }

        return usbDeviceInfo;
    }

    private Device findDevice(short vendorId, short productId) {
        boolean found = false;
        int i = 0;
        UsbDeviceInfo usbDeviceInfo = null;
        while (!found && (i < usbDeviceInfoList.size())) {
            if ((vendorId == usbDeviceInfoList.get(i).vendorId) && (productId == usbDeviceInfoList.get(i).productId)) {
                usbDeviceInfo = usbDeviceInfoList.get(i);
                found = true;
            }
            i++;
        }

        // If no perfect match, try again to at least get the vendor.
        i = 0;
        while (!found && (i < usbDeviceInfoList.size())) {
            if ((vendorId == usbDeviceInfoList.get(i).vendorId)) {
                usbDeviceInfo = new UsbDeviceInfo(usbDeviceInfoList.get(i).vendorId, usbDeviceInfoList.get(i).productId, usbDeviceInfoList.get(i).vendorDescription, "");
                found = true;
            }
            i++;
        }

        return usbDeviceInfo.device;
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
                    UsbDeviceInfo usbDeviceInfo = new UsbDeviceInfo(vendorId, (short) -1, vendorDescription, "");
                    usbDeviceInfoList.add(usbDeviceInfo);
                } else {
                    String[] smallerLines = line.substring(1).split("  ");
                    productId = hexStringToShort(smallerLines[0]);
                    productDescription = smallerLines[1];
                    UsbDeviceInfo usbDeviceInfo = new UsbDeviceInfo(vendorId, productId, vendorDescription, productDescription);
                    usbDeviceInfoList.add(usbDeviceInfo);
                }
            }
        }
    }
}
