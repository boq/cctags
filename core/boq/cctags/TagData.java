package boq.cctags;

import boq.utils.serializable.SerializableData;
import boq.utils.serializable.SerializableField;

public class TagData extends SerializableData {

    @SerializableField(nullable = true)
    public String name;

    @SerializableField(nullable = true)
    public int color;
}
