package boq.cctags;

import com.google.common.base.Throwables;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import boq.cctags.cc.*;
import boq.cctags.tag.*;
import boq.utils.lang.LangList;
import boq.utils.log.Log;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.turtle.api.TurtleAPI;

@Mod(modid = "cctags", name = "CC tags", dependencies = "required-after:ComputerCraft;after:CCTurtle;required-after:Forge@[7.0,);required-after:FML@[5.0.5,)")
@NetworkMod(channels = { Constants.ENITIY_CHANNEL_ID }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class CCTags {

    @Instance("cctags")
    public static CCTags instance;

    @SidedProxy(clientSide = "boq.cctags.client.ClientProxy", serverSide = "boq.cctags.ServerProxy")
    public static IProxy proxy;

    public static Config config;

    private int itemMiscId;
    public ItemMisc itemMisc;

    private int itemTagId;
    public ItemTag itemTag;

    private int itemReaderId;
    public ItemReader itemReader;

    private int blockPeripheralId;
    public BlockTagPeripheral blockPeripheral;

    public CreativeTabs tabTags;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Log.logger = event.getModLog();

        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try {
            cfg.load();
            itemTagId = cfg.getItem("itemTag", 27412).getInt();
            itemMiscId = cfg.getItem("itemMisc", 27413).getInt();
            itemReaderId = cfg.getItem("itemReader", 27414).getInt();
            blockPeripheralId = cfg.getBlock("blockTagPeripheral", 2324).getInt();

            config = new Config(cfg);

            tabTags = new CreativeTabTags(CreativeTabs.getNextID());
        } catch (Exception e) {
            Log.severe(e, "Error during config reading");
        } finally {
            cfg.save();
        }
    }

    @Init
    public void load(FMLInitializationEvent evt) {
        LangList.loadAll("/mods/cctags/lang/", false);

        itemTag = new ItemTag(itemTagId);
        GameRegistry.registerItem(itemTag, "cctag");

        itemMisc = new ItemMisc(itemMiscId);
        GameRegistry.registerItem(itemMisc, "cctagMisc");

        itemReader = new ItemReader(itemReaderId);
        GameRegistry.registerItem(itemReader, "cctagReader");

        blockPeripheral = new BlockTagPeripheral(blockPeripheralId);
        GameRegistry.registerBlock(blockPeripheral, ItemPeripheral.class, "tagPeripheral");

        GameRegistry.registerTileEntity(TileEntityWriter.class, "tag-writer");
        GameRegistry.registerTileEntity(TileEntityPrinter.class, "tag-printer");

        EntityRegistry.registerModEntity(EntityTag.class, "CCTag", Constants.ENTITY_TAG, this, 160, Integer.MAX_VALUE, false);

        MinecraftForge.EVENT_BUS.register(new EntityTagsListener());

        proxy.registerRenderers();
        TagIcons.instance.loadPredefinedIcons();
        Recipes.registerRecipes();
    }

    @PostInit
    public void modsLoaded(FMLPostInitializationEvent evt) {
        try {
            MountHelper.instance.copyFiles();
            TagLibrary.instance.readLibrary();
        } catch (Throwable t) {
            Throwables.propagateIfPossible(t);
        }
        for (TurtlePeripheralType type : TurtlePeripheralType.TYPES)
            TurtleAPI.registerUpgrade(type);
    }
}
