package boq.cctags.tag.access;

import boq.cctags.tag.TagData;

public interface ITagAccess {
    public boolean isValid();

    public boolean isPrintable();

    public TagData readData();

    public void writeData(TagData data, boolean updateClients);

    public int uid();

    public String name();
}
