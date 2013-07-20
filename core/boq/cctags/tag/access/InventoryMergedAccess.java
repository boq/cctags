package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.TagData;
import boq.utils.log.Log;

public class InventoryMergedAccess extends ItemAccess {
    private final InventoryItemAccess itemAccess;
    private final InventoryTagAccess tagAccess;

    public InventoryMergedAccess(IStackProvider provider) {
        super(provider);
        // no sub-access should try to use stack directly
        itemAccess = new InventoryItemAccess(null);
        tagAccess = new InventoryTagAccess(null);
    }

    @Override
    public String name() {
        return "inventory";
    }

    @Override
    public boolean isValid(ItemStack is) {
        return itemAccess.isValid(is) || tagAccess.isValid(is);
    }

    @Override
    protected TagData readData(ItemStack stack) {
        if (itemAccess.isValid(stack))
            return itemAccess.readData(stack);

        if (tagAccess.isValid(stack))
            return tagAccess.readData(stack);

        return null;
    }

    @Override
    protected void writeData(TagData data, ItemStack stack) {
        if (itemAccess.isValid(stack))
            itemAccess.writeData(data, stack);
        else if (tagAccess.isValid(stack))
            tagAccess.writeData(data, stack);
        else
            Log.warning("Invalid tag to save: %s", stack);
    }

}
