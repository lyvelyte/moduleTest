package moduleTest;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;

public class UsbDeviceInfo {
    public final short vendorId;
    public final short productId;
    public final String vendorDescription;
    public final String productDescription;
    public DeviceDescriptor descriptor;
    public Device device;

    public UsbDeviceInfo(short vendorId, short productId, String vendorDescription, String productDescription) {
        this.vendorId = vendorId;
        this.vendorDescription = vendorDescription;
        this.productId = productId;
        this.productDescription = productDescription;
    }

    public String getDescription() {
        return vendorDescription + " " + productDescription;
    }
}
