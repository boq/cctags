package boq.cctags.tag.access;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.tag.EntityTag;
import boq.cctags.tag.access.EntityTagAccess.IPositionProvider;
import boq.utils.coord.Bounds;

public class AccessUtils {
    private final static double DELTA = 0.01;
    private final static Bounds searchBounds = new Bounds(DELTA, DELTA, DELTA, 1 - DELTA, 1 - DELTA, 1 - DELTA);

    public final static ITagAccess NULL = new NullAccess();

    public static ITagAccess selectTag(World world, ForgeDirection direction, final IPositionProvider provider) {
        Vec3 position = provider.getPosition();
        AxisAlignedBB unitBox = searchBounds.getAabbFromPool(position.xCoord, position.yCoord, position.zCoord);

        ForgeDirection tagDirection = direction.getOpposite();
        for (Object o : world.getEntitiesWithinAABB(EntityTag.class, unitBox)) {
            EntityTag tag = (EntityTag)o;
            if (tag.data.side == tagDirection)
                return new EntityTagAccess(tag, provider);
        }

        return NULL;
    }
}
