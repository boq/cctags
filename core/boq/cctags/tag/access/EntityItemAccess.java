package boq.cctags.tag.access;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public class EntityItemAccess extends EntityItemStackAccess<EntityItem> {

    EntityItemAccess(EntityItem entity, IPositionProvider position, IItemStackDataAccess dataAccess) {
        super(entity, position, dataAccess);
    }

    @Override
    public String name() {
        return "item";
    }

    @Override
    protected ItemStack getItemStack(EntityItem entity) {
        return entity.getEntityItem();
    }
}
