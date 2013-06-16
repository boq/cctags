package boq.cctags;

import net.minecraftforge.common.Configuration;
import boq.cctags.cc.*;
import boq.cctags.tag.InitRegistries;
import boq.cctags.tag.ItemTag;
import boq.utils.lang.LangList;
import boq.utils.log.Log;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "cctags", name = "CC tags", dependencies = "required-after:ComputerCraft;after:CCTurtle;required-after:Forge@[7.0,);required-after:FML@[5.0.5,)")
@NetworkMod(channels = { Constants.ENITIY_CHANNEL_ID }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class CCTags {

    @Instance("cctags")
    public static CCTags instance;

    @SidedProxy(clientSide = "boq.cctags.client.ClientProxy", serverSide = "boq.cctags.ServerProxy")
    public static IProxy proxy;

    public static Config config;

    private int itemTagId;
    public ItemTag itemTag;

    private int blockPeripheralId;
    public BlockTagPeripheral blockPeripheral;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Log.logger = event.getModLog();

        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try {
            cfg.load();
            itemTagId = cfg.getItem("itemTag", 27412).getInt();
            blockPeripheralId = cfg.getBlock("blockTagPeripheral", 2324).getInt();

            config = new Config(cfg);
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

        blockPeripheral = new BlockTagPeripheral(blockPeripheralId);
        GameRegistry.registerBlock(blockPeripheral, ItemPeripheral.class, "tag-peripheral");
        GameRegistry.registerTileEntity(TileEntityWriter.class, "tag-writer");
        GameRegistry.registerTileEntity(TileEntityPrinter.class, "tag-printer");

        proxy.registerRenderers();
        InitRegistries.registerAllTheThings();
    }

    @PostInit
    public void modsLoaded(FMLPostInitializationEvent evt) {}
}
