package boq.cctags.tag.access;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityPlayerAccess extends EntityItemStackAccess<EntityPlayer> {

    public EntityPlayerAccess(EntityPlayer entity, IPositionProvider position, IItemStackDataAccess dataAccess) {
        super(entity, position, dataAccess);
    }

    @Override
    public String name() {
        return "item in hand";
    }

    @Override
    protected ItemStack getItemStack(EntityPlayer entity) {
        return entity.getHeldItem();
    }
}
