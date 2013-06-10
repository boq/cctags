package boq.cctags;

import net.minecraftforge.common.Configuration;
import boq.cctags.world.ItemTag;
import boq.utils.lang.LangList;
import boq.utils.log.Log;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "cctags", name = "CC tags", dependencies = "required-after:ComputerCraft;after:CCTurtle;required-after:Forge@[7.0,);required-after:FML@[5.0.5,)")
@NetworkMod(channels = { Constants.CHANNEL_ID }, clientSideRequired = true, serverSideRequired = false)
public class CCTags {

    @Instance("cctags")
    public static CCTags instance;

    @SidedProxy(clientSide = "boq.cctags.client.ClientProxy", serverSide = "boq.cctags.ServerProxy")
    public static IProxy proxy;

    private int itemTagId;
    public ItemTag itemTag;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Log.logger = event.getModLog();

        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try {
            cfg.load();
            itemTagId = cfg.getItem("itemTag", 27412).getInt();
        } catch (Exception e) {
            Log.severe(e, "Error during config reading");
        } finally {
            cfg.save();
        }
    }

    @Init
    public void load(FMLInitializationEvent evt) {
        LangList.loadAll("/mods/cctags/lang/");

        itemTag = new ItemTag(itemTagId);

        proxy.registerRenderers();
        InitRegistries.registerAllTheThings();
    }

    @PostInit
    public void modsLoaded(FMLPostInitializationEvent evt) {}
}
