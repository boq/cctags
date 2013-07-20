package boq.cctags.tag.access;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import boq.cctags.tag.ItemTagUtils;
import boq.cctags.tag.TagData;

import com.google.common.base.Preconditions;

public class EntityFrameItemAccess extends EntityAccess<EntityItemFrame> {

    public EntityFrameItemAccess(EntityItemFrame tag, IPositionProvider position) {
        super(tag, position);
    }

    @Override
    protected TagData readData(EntityItemFrame e) {
        ItemStack stack = e.getDisplayedItem();
        Preconditions.checkArgument(stack != null && ItemTagUtils.hasEmbeddedTag(stack), "Invalid entity item");
        return ItemTagUtils.readEmbeddedData(stack);
    }

    @Override
    protected void writeData(EntityItemFrame e, TagData data, boolean updateClients) {
        ItemStack stack = e.getDisplayedItem();
        Preconditions.checkArgument(stack != null && ItemTagUtils.hasEmbeddedTag(stack), "Invalid entity item");

        ItemTagUtils.writeEmbeddedData(stack, data);
        e.setDisplayedItem(stack);
    }

    @Override
    protected boolean isValid(EntityItemFrame entity) {
        ItemStack stack = entity.getDisplayedItem();
        return stack != null && ItemTagUtils.hasEmbeddedTag(stack);
    }

    @Override
    public String name() {
        return "framed item";
    }
}
