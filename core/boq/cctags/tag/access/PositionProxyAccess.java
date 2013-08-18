package boq.cctags.tag.access;

import net.minecraft.util.Vec3;
import boq.cctags.Constants;
import boq.cctags.tag.TagData;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;

public class PositionProxyAccess implements ITagAccess {

    private final IPositionProvider reader;
    private final IPositionProvider computer;
    private final ITagAccess wrappedAccess;

    public PositionProxyAccess(IPositionProvider reader, IPositionProvider computer, ITagAccess wrappedAccess) {
        this.reader = reader;
        this.computer = computer;
        this.wrappedAccess = wrappedAccess;
    }

    @Override
    public boolean isValid() {
        if (!reader.isValid() ||
                !computer.isValid() ||
                reader.getWorld() != computer.getWorld())
            return false;

        Vec3 vA = reader.getPosition();
        Vec3 vB = computer.getPosition();
        if (vA.distanceTo(vB) > Constants.READER_READ_DISTANCE)
            return false;

        return wrappedAccess.isValid();
    }

    @Override
    public boolean isPrintable() {
        return wrappedAccess.isPrintable();
    }

    @Override
    public TagData readData() {
        return wrappedAccess.readData();
    }

    @Override
    public void writeData(TagData data, boolean updateClients) {
        wrappedAccess.writeData(data, updateClients);
    }

    @Override
    public int uid() {
        return wrappedAccess.uid();
    }

    @Override
    public String name() {
        return "Wrapped: " + wrappedAccess.name();
    }

}
