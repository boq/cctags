package boq.cctags.tag.access;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.Constants;
import boq.cctags.tag.TagData;

import com.google.common.base.Preconditions;

public abstract class EntityAccess<E extends Entity> implements ITagAccess {

    public interface IPositionProvider {
        public Vec3 getPosition();

        public ForgeDirection getOrientation();

        public World getWorld();

        public boolean isValid();
    }

    private WeakReference<E> entity;
    private final IPositionProvider position;

    public EntityAccess(E entity, IPositionProvider position) {
        this.entity = new WeakReference<E>(entity);
        this.position = position;
    }

    @Override
    public boolean isValid() {
        if (!position.isValid())
            return false;

        E e = entity.get();

        if (e == null)
            return false;

        if (!e.isDead && isValid(e)) {
            Vec3 ownPos = position.getPosition();
            Vec3 tagPos = Vec3.createVectorHelper(e.posX, e.posY, e.posZ);

            if (ownPos.distanceTo(tagPos) < Constants.ENTITY_READ_DISTANCE)
                return true;
        }

        entity.clear();
        return false;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    protected abstract boolean isValid(E entity);

    @Override
    public final TagData readData() {
        E e = entity.get();
        Preconditions.checkNotNull(e, "Tag source dissapeared");
        return readData(e);
    }

    protected abstract TagData readData(E entity);

    @Override
    public final void writeData(TagData data, boolean updateClients) {
        E e = entity.get();
        Preconditions.checkNotNull(e, "Tag source dissapeared");
        writeData(e, data, updateClients);
    }

    protected abstract void writeData(E entity, TagData data, boolean updateClients);

    @Override
    public int uid() {
        return readData().uid(entity);
    }

}
