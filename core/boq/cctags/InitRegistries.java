package boq.cctags;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import boq.cctags.cc.ItemMisc;
import boq.cctags.cc.PeripheralType;
import boq.cctags.tag.*;
import cpw.mods.fml.common.registry.EntityRegistry;

public class InitRegistries {
    private InitRegistries() {}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void registerRecipes() {
        final CCTags mod = CCTags.instance;
        final CraftingManager manager = CraftingManager.getInstance();
        List recipes = manager.getRecipeList();
        recipes.add(new DyeRecipe());

        ItemStack itemTag64 = new ItemStack(mod.itemTag, 16, TagSize.TAG_64.ordinal());
        ItemTag.setupDefaultTags(itemTag64);
        recipes.add(new ShapedOreRecipe(itemTag64, "PSP", "PRP", "PIP", 'S', Item.slimeBall, 'P', Item.paper, 'R', Item.redstone, 'I', Item.ingotIron));

        ItemStack itemTag256 = new ItemStack(mod.itemTag, 8, TagSize.TAG_256.ordinal());
        ItemTag.setupDefaultTags(itemTag256);
        recipes.add(new ShapedOreRecipe(itemTag256, "PSP", "RRR", "PIP", 'S', Item.slimeBall, 'P', Item.paper, 'R', Item.redstone, 'I', Item.ingotIron));

        ItemStack itemTag1K = new ItemStack(mod.itemTag, 4, TagSize.TAG_1K.ordinal());
        ItemTag.setupDefaultTags(itemTag1K);
        recipes.add(new ShapedOreRecipe(itemTag1K, "PSP", "RRR", "PGP", 'S', Item.slimeBall, 'P', Item.paper, 'R', Item.redstone, 'G', Item.goldNugget));

        ItemStack itemTag64K = new ItemStack(mod.itemTag, 8, TagSize.TAG_4K.ordinal());
        ItemTag.setupDefaultTags(itemTag64K);
        recipes.add(new ShapedOreRecipe(itemTag64K, "PSP", "RRR", "PGP", 'S', Item.slimeBall, 'P', Item.paper, 'R', Item.redstone, 'G', Item.ingotGold));

        ItemStack itemWriterPcb = new ItemStack(mod.itemMisc, 1, ItemMisc.Subtype.WRITER_PCB.ordinal());
        manager.addRecipe(itemWriterPcb, "GGG", " R ", " I ", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron);
        PeripheralType.WRITER.craftingItem = itemWriterPcb;

        ItemStack itemPrinterPcb = new ItemStack(mod.itemMisc, 1, ItemMisc.Subtype.PRINTER_PCB.ordinal());
        manager.addRecipe(itemPrinterPcb, "GGG", "RRR", "IBI", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron, 'B', new ItemStack(Item.dyePowder, 1, 0));
        PeripheralType.PRINTER.craftingItem = itemPrinterPcb;

        ItemStack itemWriter = mod.blockPeripheral.getDefaultItem(PeripheralType.WRITER);
        manager.addRecipe(itemWriter, "SSS", "SRS", "SWS", 'S', Block.stone, 'R', Item.redstone, 'W', itemWriterPcb);

        ItemStack itemPrinter = mod.blockPeripheral.getDefaultItem(PeripheralType.PRINTER);
        manager.addShapelessRecipe(itemPrinter, itemWriter, itemPrinterPcb);
    }

    private static void registerEntities() {
        final CCTags mod = CCTags.instance;
        EntityRegistry.registerModEntity(EntityTag.class, "CCTag", Constants.ENTITY_TAG, mod, 160, Integer.MAX_VALUE, false);
    }

    public static void registerAllTheThings() {
        registerRecipes();
        registerEntities();
    }
}
