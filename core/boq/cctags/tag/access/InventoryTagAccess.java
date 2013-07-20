package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.*;

public class InventoryTagAccess extends ItemAccess {

    public InventoryTagAccess(IStackProvider provider) {
        super(provider);
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.getItem() instanceof ItemTag;
    }

    @Override
    protected TagData readData(ItemStack stack) {
        return ItemTagUtils.readData(stack);
    }

    @Override
    protected void writeData(TagData data, ItemStack stack) {
        ItemTagUtils.writeData(stack, data);
    }

    @Override
    public String name() {
        return "inventory tag";
    }
}
