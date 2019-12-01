package moduleTest;

public class Application {
    public static void main(final String[] args) throws Exception {
        USBManager usbManager = new USBManager();
        usbManager.listDevices();
    }
}