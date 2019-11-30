package moduleTest;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

public class Application {
    private static void dumpName(final UsbDevice device)
            throws UnsupportedEncodingException, UsbException
    {
        // Read the string descriptor indices from the device descriptor.
        // If they are missing then ignore the device.
        final UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
        final byte iManufacturer = desc.iManufacturer();
        final byte iProduct = desc.iProduct();
        if (iManufacturer == 0 || iProduct == 0) return;

        // Dump the device name
        System.out.println(device.getString(iManufacturer) + " "
                + device.getString(iProduct));
    }

    /**
     * Processes the specified USB device.
     *
     * @param device
     *            The USB device to process.
     */
    private static void processDevice(final UsbDevice device)
    {
        // When device is a hub then process all child devices
        if (device.isUsbHub())
        {
            final UsbHub hub = (UsbHub) device;
            for (UsbDevice child: (List<UsbDevice>) hub.getAttachedUsbDevices())
            {
                processDevice(child);
            }
        }

        // When device is not a hub then dump its name.
        else
        {
            try
            {
                dumpName(device);
            }
            catch (Exception e)
            {
                // On Linux this can fail because user has no write permission
                // on the USB device file. On Windows it can fail because
                // no libusb device driver is installed for the device
                System.err.println("Ignoring problematic device: " + e);
            }
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            Command-line arguments (Ignored)
     * @throws UsbException
     *             When an USB error was reported which wasn't handled by this
     *             program itself.
     */
    public static void main(final String[] args) throws UsbException
    {
        // Get the USB services and dump information about them
        final UsbServices services = UsbHostManager.getUsbServices();

        // Dump the root USB hub
        processDevice(services.getRootUsbHub());
    }
}