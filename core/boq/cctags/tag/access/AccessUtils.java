package boq.cctags.tag.access;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.tag.EntityTag;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;
import boq.utils.coord.Bounds;

public class AccessUtils {
    private final static double DELTA = 0.01;
    private final static Bounds turtleSearchBounds = new Bounds(DELTA, DELTA, DELTA, 1 - DELTA, 1 - DELTA, 1 - DELTA);
    private final static Bounds itemSearchBounds = new Bounds(-DELTA, -DELTA, -DELTA, 1 + DELTA, 1 + DELTA, 1 + DELTA);

    public final static ITagAccess NULL = new NullAccess();

    private static ITagAccess checkItemAccess(Object o, IPositionProvider provider, IItemStackDataAccess dataAccess) {
        ITagAccess access = null;

        if (o instanceof EntityItem)
            access = new EntityItemAccess((EntityItem)o, provider, dataAccess);
        else if (o instanceof EntityItemFrame)
            access = new EntityItemFrameAccess((EntityItemFrame)o, provider, dataAccess);
        else if (o instanceof EntityPlayer)
            access = new EntityPlayerAccess((EntityPlayer)o, provider, dataAccess);
        else
            return null;

        return access.isValid() ? access : null;
    }

    public static ITagAccess selectTag(IPositionProvider provider, ForgeDirection direction) {
        if (!provider.isValid())
            return NULL;

        Vec3 position = provider.getPosition();
        World world = provider.getWorld();
        AxisAlignedBB turtleUnitBox = turtleSearchBounds.getAabbFromPool(position.xCoord, position.yCoord, position.zCoord);

        ForgeDirection tagDirection = direction.getOpposite();
        for (Object o : world.getEntitiesWithinAABB(EntityTag.class, turtleUnitBox)) {
            EntityTag tag = (EntityTag)o;
            if (tag.data.side == tagDirection)
                return new EntityTagAccess(tag, provider);
        }

        AxisAlignedBB blockUnitBox = itemSearchBounds.getAabbFromPool(position.xCoord + direction.offsetX,
                position.yCoord + direction.offsetY,
                position.zCoord + direction.offsetZ);

        for (Object o : world.getEntitiesWithinAABB(Entity.class, blockUnitBox)) {
            {
                ITagAccess access = checkItemAccess(o, provider, ItemStackNormalAccess.instance);
                if (access != null && access.isValid())
                    return access;
            }

            {
                ITagAccess access = checkItemAccess(o, provider, ItemStackEmbeddedAccess.instance);
                if (access != null && access.isValid())
                    return access;
            }

            if (o instanceof EntityLivingBase) {
                ITagAccess access = new EntityLivingAccess((EntityLivingBase)o, provider);
                if (access.isValid())
                    return access;
            }
        }

        return NULL;
    }

    public final static IItemStackDataAccess mergedAccess = new IItemStackDataAccess.MergedAccess(new ItemStackNormalAccess(), new ItemStackEmbeddedAccess());
}
