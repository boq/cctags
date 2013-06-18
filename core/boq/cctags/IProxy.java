package boq.cctags;

import java.io.File;

import net.minecraft.world.World;

public interface IProxy {
    public void registerRenderers();

    public World getClientWorld();

    public boolean isServer();

    public File getMcFolder();
}
