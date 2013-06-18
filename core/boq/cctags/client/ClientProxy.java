package boq.cctags.client;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.server.ServerListenThread;
import net.minecraft.server.ThreadMinecraftServer;
import net.minecraft.world.World;
import boq.cctags.IProxy;
import boq.cctags.tag.EntityTag;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy {

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTag.class, new EntityTagRenderer());
        TagIcons.instance.loadPredefinedIcons();
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public boolean isServer() {
        Thread thr = Thread.currentThread();
        return thr instanceof ThreadMinecraftServer || thr instanceof ServerListenThread;
    }

    @Override
    public File getMcFolder() {
        return Minecraft.getMinecraftDir();
    }
}
