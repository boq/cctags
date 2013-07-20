package boq.cctags.tag.access;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.tag.EntityTag;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;
import boq.cctags.tag.access.ItemAccess.IStackProvider;
import boq.utils.coord.Bounds;

public class AccessUtils {
    private final static double DELTA = 0.01;
    private final static Bounds turtleSearchBounds = new Bounds(DELTA, DELTA, DELTA, 1 - DELTA, 1 - DELTA, 1 - DELTA);
    private final static Bounds itemSearchBounds = new Bounds(-DELTA, -DELTA, -DELTA, 1 + DELTA, 1 + DELTA, 1 + DELTA);

    public final static ITagAccess NULL = new NullAccess();

    public static ITagAccess selectTag(World world, ForgeDirection direction, IPositionProvider provider) {
        Vec3 position = provider.getPosition();
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
            ITagAccess access;
            if (o instanceof EntityItem)
                access = new EntityItemAccess((EntityItem)o, provider);
            else if (o instanceof EntityItemFrame) {
                EntityItemFrame frame = (EntityItemFrame)o;
                access = new EntityFrameTagAccess(frame, provider);
                if (access.isValid())
                    return access;

                access = new EntityFrameItemAccess(frame, provider);
                if (access.isValid())
                    return access;
            }
            else if (o instanceof EntityLivingBase)
                access = new EntityLivingAccess((EntityLivingBase)o, provider);
            else
                continue;

            if (access.isValid())
                return access;
        }

        return NULL;
    }

    public static ITagAccess selectSlot(IStackProvider provider) {
        ITagAccess access = new InventoryTagAccess(provider);
        if (access.isValid())
            return access;

        access = new InventoryItemAccess(provider);
        if (access.isValid())
            return access;

        return NULL;

    }
}
