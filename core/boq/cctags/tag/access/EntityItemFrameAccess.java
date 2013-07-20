package boq.cctags.tag.access;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;

public class EntityItemFrameAccess extends EntityItemStackAccess<EntityItemFrame> {

    public EntityItemFrameAccess(EntityItemFrame entity, IPositionProvider position, IItemStackDataAccess dataAccess) {
        super(entity, position, dataAccess);
    }

    @Override
    public String name() {
        return "framed item";
    }

    @Override
    protected ItemStack getItemStack(EntityItemFrame entity) {
        return entity.getDisplayedItem();
    }
}
