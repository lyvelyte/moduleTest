package moduleTest;

import java.util.List;

public class Application {
    public static void main(final String[] args) throws Exception {
        USBManager usbManager = new USBManager();
        List<UsbDeviceInfo> attachedUsbDevices = usbManager.listDevices();
        for (UsbDeviceInfo usbDeviceInfo : attachedUsbDevices) {
            System.out.println(usbDeviceInfo.getDescription());
        }
    }
}