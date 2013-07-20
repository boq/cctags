package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.ItemTagUtils;
import boq.cctags.tag.TagData;

import com.google.common.base.Preconditions;

public class InventoryItemAccess extends ItemAccess {

    public InventoryItemAccess(IStackProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return "inventory item";
    }

    @Override
    public boolean isValid(ItemStack is) {
        return ItemTagUtils.hasEmbeddedTag(is);
    }

    @Override
    protected TagData readData(ItemStack stack) {
        Preconditions.checkNotNull(stack.stackTagCompound);
        return ItemTagUtils.readEmbeddedData(stack);
    }

    @Override
    protected void writeData(TagData data, ItemStack stack) {
        ItemTagUtils.writeEmbeddedData(stack, data);
    }

}
