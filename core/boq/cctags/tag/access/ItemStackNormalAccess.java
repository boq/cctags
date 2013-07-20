package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.*;

public class ItemStackNormalAccess implements IItemStackDataAccess {

    public static final ItemStackNormalAccess instance = new ItemStackNormalAccess();

    @Override
    public TagData readData(ItemStack stack) {
        return ItemTagUtils.readData(stack);
    }

    @Override
    public void writeData(ItemStack stack, TagData data) {
        ItemTagUtils.writeData(stack, data);
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.getItem() instanceof ItemTag;
    }

}
