package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.ItemTagUtils;
import boq.cctags.tag.TagData;

public class ItemStackEmbeddedAccess implements IItemStackDataAccess {

    public static final ItemStackEmbeddedAccess instance = new ItemStackEmbeddedAccess();

    @Override
    public TagData readData(ItemStack stack) {
        return ItemTagUtils.readEmbeddedData(stack);
    }

    @Override
    public void writeData(ItemStack stack, TagData data) {
        ItemTagUtils.writeEmbeddedData(stack, data);
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return ItemTagUtils.hasEmbeddedTag(stack);
    }

}
