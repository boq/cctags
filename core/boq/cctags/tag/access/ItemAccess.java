package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.ItemTagUtils;
import boq.cctags.tag.TagData;

public class ItemAccess implements ITagAccess {

    public interface IStackProvider {
        public ItemStack getStack();
    }

    private final IStackProvider provider;
    private final IItemStackDataAccess access;

    public ItemAccess(IStackProvider provider, IItemStackDataAccess access) {
        this.provider = provider;
        this.access = access;
    }

    @Override
    public boolean isValid() {
        ItemStack stack = provider.getStack();
        return stack != null && stack.stackSize == 1 && access.isValid(stack);
    }

    @Override
    public final TagData readData() {
        ItemStack stack = provider.getStack();
        return access.readData(stack);
    }

    @Override
    public final void writeData(TagData data, boolean updateClients) {
        ItemStack stack = provider.getStack();
        access.writeData(stack, data);
    }

    @Override
    public int uid() {
        ItemStack stack = provider.getStack();
        return ItemTagUtils.readData(stack).uid(stack);
    }

    @Override
    public String name() {
        return "inventory item";
    }
}
