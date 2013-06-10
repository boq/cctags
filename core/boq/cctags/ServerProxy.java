package boq.cctags;

import net.minecraft.world.World;

public class ServerProxy implements IProxy {

    @Override
    public void registerRenderers() {}

    @Override
    public World getClientWorld() {
        return null;
    }

    @Override
    public boolean isServer() {
        return false;
    }

}
