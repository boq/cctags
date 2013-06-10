package boq.utils.misc;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public enum PlayerOrientation {
    SOUTH, WEST, NORTH, EAST;

    public final static PlayerOrientation[] values = values();

    public static PlayerOrientation getEntityOrientation(Entity entity) {
        int rotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return values[rotation];
    }
}
