package boq.cctags;

import java.io.File;

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
        return true;
    }

    @Override
    public File getMcFolder() {
        return new File(".");
    }
}
