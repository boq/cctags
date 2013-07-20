package boq.cctags.cc;

import net.minecraft.item.ItemStack;
import boq.utils.serializable.SerializableData;
import boq.utils.serializable.SerializableField;

public class WriterData extends SerializableData {
    @SerializableField(nullable = true)
    public ItemStack insertedItem;

}
