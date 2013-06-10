package boq.utils.render;

import java.util.EnumMap;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

@SuppressWarnings("serial")
public class SideMap<T extends Icon> extends EnumMap<ForgeDirection, T> {

    public static class IconMap extends SideMap<Icon> {}

    public SideMap() {
        super(ForgeDirection.class);
    }

    public SideMap<T> copy() {
        SideMap<T> c = new SideMap<T>();
        c.putAll(this);
        return c;
    }

    @Override
    public T get(Object side) {
        T result = super.get(side);
        if (result != null)
            return result;

        return super.get(ForgeDirection.UNKNOWN);
    }

    private static final ForgeDirection[] directions = ForgeDirection.values();

    public T getTextureForSide(int side) {
        return get(directions[side]);
    }

    public SideMap<T> setDefault(T coord) {
        put(ForgeDirection.UNKNOWN, coord);
        return this;
    }

}
