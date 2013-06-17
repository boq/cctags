package boq.cctags.tag;

import net.minecraftforge.common.ForgeDirection;
import boq.utils.misc.Rotation;
import boq.utils.serializable.SerializableData;
import boq.utils.serializable.SerializableField;

public class TagData extends SerializableData {

    public static final int EXCLUDE_IN_ITEM_NBT = SerializableField.USER_DEFINED_0;
    public static final int CLIENT_UPDATE = SerializableField.USER_DEFINED_1;

    @SerializableField
    public int color;
    public static final String TAG_COLOR = "color";

    @SerializableField(nullable = true)
    public String label;
    public static final String TAG_LABEL = "label";

    @SerializableField(nullable = true, flags = SerializableField.SERIALIZABLE | CLIENT_UPDATE)
    public String icon;
    public static final String TAG_ICON = "icon";

    @SerializableField(nullable = true, flags = SerializableField.NBT_SERIALIZABLE)
    public String contents;

    @SerializableField(flags = SerializableField.NBT_SERIALIZABLE | EXCLUDE_IN_ITEM_NBT)
    public TagSize tagSize;

    @SerializableField(flags = EXCLUDE_IN_ITEM_NBT | SerializableField.SERIALIZABLE)
    public ForgeDirection side;

    @SerializableField(flags = EXCLUDE_IN_ITEM_NBT | SerializableField.SERIALIZABLE | CLIENT_UPDATE)
    public Rotation rotation;
}