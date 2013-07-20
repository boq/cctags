package boq.cctags.tag.access;

import boq.cctags.tag.TagData;

public class NullAccess implements ITagAccess {

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public TagData readData() {
        throw new IllegalAccessError();
    }

    @Override
    public void writeData(TagData data, boolean updateClients) {
        throw new IllegalAccessError();
    }

    @Override
    public int uid() {
        throw new IllegalAccessError();
    }

}
