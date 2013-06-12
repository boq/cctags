package boq.cctags;

import boq.cctags.world.TagSize;
import boq.utils.serializable.SerializableData;
import boq.utils.serializable.SerializableField;

public class TagData extends SerializableData {

    public static final int EXCLUDE_FROM_INITIAL = SerializableField.USER_DEFINED_0;

    @SerializableField
    public int color;
    public static final String TAG_COLOR = "color";

    @SerializableField(nullable = true)
    public String icon;
    public static final String TAG_ICON = "icon";

    @SerializableField(nullable = true, flags = SerializableField.NBT_SERIALIZABLE)
    public String name;

    @SerializableField(flags = SerializableField.NBT_SERIALIZABLE | EXCLUDE_FROM_INITIAL)
    public TagSize tagSize;
}
