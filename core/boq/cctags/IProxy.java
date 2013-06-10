package boq.cctags;

import net.minecraft.world.World;

public interface IProxy {
    public void registerRenderers();

    public World getClientWorld();

    boolean isServer();
}
