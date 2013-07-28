package boq.cctags.cc;

import net.minecraft.item.ItemStack;
import boq.utils.serializable.SerializableData;
import boq.utils.serializable.SerializableField;

public class PrinterData extends SerializableData implements PrinterHelper.Printer {
    @SerializableField(nullable = true)
    public ItemStack insertedItem;

    @SerializableField
    public int inkLevel;

    @Override
    public void setInkLevel(int inkLevel) {
        this.inkLevel = inkLevel;
    }

    @Override
    public int getInkLevel() {
        return inkLevel;
    }
}
