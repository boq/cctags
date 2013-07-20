package boq.cctags.tag.access;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import boq.cctags.tag.*;

import com.google.common.base.Preconditions;

public class EntityItemAccess extends EntityAccess<EntityItem> {

    public EntityItemAccess(EntityItem tag, IPositionProvider position) {
        super(tag, position);
    }

    @Override
    protected TagData readData(EntityItem e) {
        ItemStack stack = e.getEntityItem();
        Preconditions.checkArgument(stack != null && stack.getItem() instanceof ItemTag, "Invalid entity item");
        return ItemTagUtils.readData(stack);
    }

    @Override
    protected void writeData(EntityItem e, TagData data, boolean updateClients) {
        ItemStack stack = e.getEntityItem();
        Preconditions.checkArgument(stack != null && stack.getItem() instanceof ItemTag, "Invalid entity item");

        ItemTagUtils.writeData(stack, data);
        e.setEntityItemStack(stack);
    }

    @Override
    protected boolean isValid(EntityItem entity) {
        ItemStack stack = entity.getEntityItem();
        return stack != null && stack.getItem() instanceof ItemTag;
    }

    @Override
    public String name() {
        return "item";
    }
}
