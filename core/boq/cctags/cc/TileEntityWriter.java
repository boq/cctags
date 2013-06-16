package boq.cctags.cc;

public class TileEntityWriter extends TileEntityPeripheral<WriterData> {

    public TileEntityWriter() {
        super(new WriterData());
    }

    private final static String[] methods = commonMethods;

    @Override
    public String[] getMethodNames() {
        return methods;
    }
}
