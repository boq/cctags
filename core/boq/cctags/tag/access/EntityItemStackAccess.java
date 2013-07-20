package boq.cctags.tag.access;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import boq.cctags.tag.TagData;

import com.google.common.base.Preconditions;

public abstract class EntityItemStackAccess<E extends Entity> extends EntityAccess<E> {

    private final IItemStackDataAccess dataAccess;

    public EntityItemStackAccess(E entity, IPositionProvider position, IItemStackDataAccess dataAccess) {
        super(entity, position);
        this.dataAccess = dataAccess;
    }

    protected abstract ItemStack getItemStack(E entity);

    @Override
    protected boolean isValid(E entity) {
        ItemStack is = getItemStack(entity);
        return is != null && dataAccess.isValid(is);
    }

    @Override
    protected TagData readData(E entity) {
        ItemStack is = getItemStack(entity);
        Preconditions.checkArgument(is != null && dataAccess.isValid(is), "Invalid entity item");
        return dataAccess.readData(is);
    }

    @Override
    protected void writeData(E entity, TagData data, boolean updateClients) {
        ItemStack is = getItemStack(entity);
        Preconditions.checkArgument(is != null && dataAccess.isValid(is), "Invalid entity item");
        dataAccess.writeData(is, data);
    }
}
