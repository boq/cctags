package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.ItemTagUtils;
import boq.cctags.tag.TagData;

public abstract class ItemAccess implements ITagAccess {

    public interface IStackProvider {
        public ItemStack getStack();
    }

    private final IStackProvider provider;

    public ItemAccess(IStackProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean isValid() {
        ItemStack stack = provider.getStack();
        return stack != null && stack.stackSize == 1 && isValid(stack);
    }

    public abstract boolean isValid(ItemStack is);

    @Override
    public final TagData readData() {
        ItemStack stack = provider.getStack();
        return readData(stack);
    }

    protected abstract TagData readData(ItemStack stack);

    @Override
    public final void writeData(TagData data, boolean updateClients) {
        ItemStack stack = provider.getStack();
        writeData(data, stack);
    }

    protected abstract void writeData(TagData data, ItemStack stack);

    @Override
    public int uid() {
        ItemStack stack = provider.getStack();
        return ItemTagUtils.readData(stack).uid(stack);
    }
}
