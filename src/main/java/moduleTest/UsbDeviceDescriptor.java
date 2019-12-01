package moduleTest;

public class UsbDeviceDescriptor {
    public final short vendorId;
    public final short productId;
    public final String vendorDescription;
    public final String productDescription;

    public UsbDeviceDescriptor(short vendorId, short productId, String vendorDescription, String productDescription) {
        this.vendorId = vendorId;
        this.vendorDescription = vendorDescription;
        this.productId = productId;
        this.productDescription = productDescription;
    }
}
