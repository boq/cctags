package boq.cctags.cc;

import net.minecraftforge.common.ForgeDirection;

public class SidesHelper {

    public static ForgeDirection localToWorld(ForgeDirection front, String direction) {
        if (direction == null || "front".equals(direction))
            return front;

        if ("top".equals(direction))
            return ForgeDirection.UP;

        if ("bottom".equals(direction))
            return ForgeDirection.DOWN;

        if ("back".equals(direction))
            return front.getOpposite();

        if ("left".equals(direction))
            return front.getRotation(ForgeDirection.UP);

        if ("right".equals(direction))
            return front.getRotation(ForgeDirection.DOWN);

        throw new IllegalArgumentException("Invalid direction, must be one of: top, down, front, back, left, right");
    }

}
