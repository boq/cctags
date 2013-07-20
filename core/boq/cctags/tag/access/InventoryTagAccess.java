package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.ItemTag;
import boq.cctags.tag.TagData;

public class InventoryTagAccess implements ITagAccess {

    public interface IStackProvider {
        public ItemStack getStack();
    }

    private final IStackProvider provider;

    public InventoryTagAccess(IStackProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean isValid() {
        ItemStack stack = provider.getStack();
        return stack != null && stack.getItem() instanceof ItemTag && stack.stackSize == 1;
    }

    @Override
    public TagData readData() {
        ItemStack stack = provider.getStack();
        return ItemTag.readData(stack);
    }

    @Override
    public void writeData(TagData data, boolean updateClients) {
        ItemStack stack = provider.getStack();
        ItemTag.writeData(stack, data);
    }

    @Override
    public int uid() {
        ItemStack stack = provider.getStack();
        return ItemTag.readData(stack).uid(stack);
    }
}
