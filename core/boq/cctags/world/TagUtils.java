package boq.cctags.world;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableList;

public class TagUtils {

    private final static double MARGIN = 0.01;

    public static List<EntityTag> getBlockTags(World world, int x, int y, int z) {
        AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(x + MARGIN, y + MARGIN, z + MARGIN, x + 1 - MARGIN, y + 1 - MARGIN, z + 1 - MARGIN);

        ImmutableList.Builder<EntityTag> builder = ImmutableList.builder();
        for (Object o : world.getEntitiesWithinAABB(EntityTag.class, aabb))
            builder.add((EntityTag)o);

        return builder.build();
    }
}
