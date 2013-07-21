package boq.cctags.tag;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.CCTags;
import boq.utils.misc.Rotation;
import boq.utils.serializable.SerializableData;
import boq.utils.serializable.SerializableField;

import com.google.common.base.Strings;

public class TagData extends SerializableData {

    public static final int EXCLUDE_IN_ITEM_NBT = SerializableField.USER_DEFINED_0;
    public static final int CLIENT_UPDATE = SerializableField.USER_DEFINED_1;
    public static final int EXCLUDE_IN_EMBEDDED_TAG = SerializableField.USER_DEFINED_2;

    @SerializableField(flags = SerializableField.SERIALIZABLE | CLIENT_UPDATE)
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

    @SerializableField(flags = SerializableField.SERIALIZABLE | EXCLUDE_IN_ITEM_NBT)
    public TagType tagType;

    @SerializableField(flags = SerializableField.SERIALIZABLE | EXCLUDE_IN_ITEM_NBT | EXCLUDE_IN_EMBEDDED_TAG)
    public ForgeDirection side;

    @SerializableField(flags = SerializableField.SERIALIZABLE | EXCLUDE_IN_ITEM_NBT | EXCLUDE_IN_EMBEDDED_TAG | CLIENT_UPDATE)
    public Rotation rotation;

    private int uid = -1;

    public int uid(Object container) {
        if (uid <= 0) {
            int id = container == null ? 0xDEADED : container.hashCode() & 0xFFFFFF;
            uid = 0x42000000 | id;
        }

        return uid;
    }

    public String tagDescription(Object owner) {
        String printLabel;

        if (Strings.isNullOrEmpty(label)) {
            String un = CCTags.instance.itemTag.getUnlocalizedName() + ".name";
            printLabel = StatCollector.translateToLocal(un);
        } else
            printLabel = label;

        String printContents;

        if (Strings.isNullOrEmpty(contents))
            printContents = StatCollector.translateToLocal("cctag.empty");
        else
            printContents = contents;

        return String.format("%s (#%06X, %s): %s", printLabel, uid(this), tagSize.name, Strings.nullToEmpty(printContents));
    }
}
