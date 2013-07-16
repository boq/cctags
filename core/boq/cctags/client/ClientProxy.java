package boq.cctags.client;

import net.minecraft.server.ServerListenThread;
import net.minecraft.server.ThreadMinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import boq.cctags.IProxy;
import boq.cctags.tag.EntityTag;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy {

    @Override
    public void registerRenderers() {
        EntityTagRenderer tagRender = new EntityTagRenderer();
        RenderingRegistry.registerEntityRenderingHandler(EntityTag.class, tagRender);
        MinecraftForge.EVENT_BUS.register(tagRender);
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
}
