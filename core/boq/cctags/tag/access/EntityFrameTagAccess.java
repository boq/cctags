package boq.cctags.tag.access;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import boq.cctags.tag.*;

import com.google.common.base.Preconditions;

public class EntityFrameTagAccess extends EntityAccess<EntityItemFrame> {

    public EntityFrameTagAccess(EntityItemFrame tag, IPositionProvider position) {
        super(tag, position);
    }

    @Override
    protected TagData readData(EntityItemFrame e) {
        ItemStack stack = e.getDisplayedItem();
        Preconditions.checkArgument(stack != null && stack.getItem() instanceof ItemTag, "Invalid entity item");
        return ItemTagUtils.readData(stack);
    }

    @Override
    protected void writeData(EntityItemFrame e, TagData data, boolean updateClients) {
        ItemStack stack = e.getDisplayedItem();
        Preconditions.checkArgument(stack != null && stack.getItem() instanceof ItemTag, "Invalid entity item");

        ItemTagUtils.writeData(stack, data);
        e.setDisplayedItem(stack);
    }

    @Override
    protected boolean isValid(EntityItemFrame entity) {
        ItemStack stack = entity.getDisplayedItem();
        return stack != null && stack.getItem() instanceof ItemTag;
    }

    @Override
    public String name() {
        return "framed tag";
    }
}
