package boq.cctags;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import boq.cctags.cc.ItemMisc.Subtype;
import boq.cctags.cc.PeripheralType;
import boq.cctags.tag.*;

import com.google.common.collect.ObjectArrays;

public class Recipes {
    private Recipes() {}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static ItemStack createTagStack(List recipes, ItemTag item, int count, TagSize size) {
        int damage = ItemTagUtils.calculateDamage(TagType.NORMAL, size);
        ItemStack result = new ItemStack(item, count, damage);
        ItemTagUtils.setupDefaultTags(result);

        recipes.add(new BigTagRecipe(result, size));
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addTagRecipes(List recipes, ItemStack stack, Object... recipe) {
        Object[] recipeWithP = ObjectArrays.concat(recipe, 'P');
        recipes.add(new ShapedOreRecipe(stack.copy(), ObjectArrays.concat(recipeWithP, Item.paper)));
        ItemStack glassStack = ItemTagUtils.upgradeToType(stack, TagType.GLASS);
        recipes.add(new ShapedOreRecipe(glassStack, ObjectArrays.concat(recipeWithP, Block.thinGlass)));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void registerRecipes() {
        final CCTags mod = CCTags.instance;
        final CraftingManager manager = CraftingManager.getInstance();
        List recipes = manager.getRecipeList();
        recipes.add(new DyeRecipe());

        ItemStack itemTag64 = createTagStack(recipes, mod.itemTag, 16, TagSize.TAG_64);
        addTagRecipes(recipes, itemTag64, "PSP", "PRP", "PIP", 'S', Item.slimeBall, 'R', Item.redstone, 'I', Item.ingotIron);

        ItemStack itemTag256 = createTagStack(recipes, mod.itemTag, 8, TagSize.TAG_256);
        addTagRecipes(recipes, itemTag256, "PSP", "RRR", "PIP", 'S', Item.slimeBall, 'R', Item.redstone, 'I', Item.ingotIron);

        ItemStack itemTag1K = createTagStack(recipes, mod.itemTag, 4, TagSize.TAG_1K);
        addTagRecipes(recipes, itemTag1K, "PSP", "RRR", "PGP", 'S', Item.slimeBall, 'R', Item.redstone, 'G', Item.goldNugget);

        ItemStack itemTag64K = createTagStack(recipes, mod.itemTag, 8, TagSize.TAG_4K);
        addTagRecipes(recipes, itemTag64K, "PSP", "RRR", "PGP", 'S', Item.slimeBall, 'R', Item.redstone, 'G', Item.ingotGold);

        ItemStack itemHandheld = new ItemStack(mod.itemReader);
        recipes.add(new ShapedOreRecipe(itemHandheld, "WRW", "IDG", "WRW", 'W', "plankWood", 'R', Item.redstone, 'G', Block.thinGlass, 'D', "dyeGreen", 'I', Item.ingotIron));

        ItemStack itemHandheldOld = mod.itemMisc.getStack(Subtype.HANDHELD_OLD);
        manager.addShapelessRecipe(itemHandheld, itemHandheldOld);

        ItemStack itemWriterPcb = mod.itemMisc.getStack(Subtype.WRITER_PCB);
        manager.addRecipe(itemWriterPcb, "GGG", " R ", " I ", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron);
        PeripheralType.WRITER.craftingItem = itemWriterPcb;

        ItemStack itemPrinterPcb = mod.itemMisc.getStack(Subtype.PRINTER_PCB);
        manager.addRecipe(itemPrinterPcb, "GGG", "RRR", "IBI", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron, 'B', new ItemStack(Item.dyePowder, 1, 0));
        PeripheralType.PRINTER.craftingItem = itemPrinterPcb;

        ItemStack itemWriter = mod.blockPeripheral.getDefaultItem(PeripheralType.WRITER);
        manager.addRecipe(itemWriter, "SSS", "SRS", "SWS", 'S', Block.stone, 'R', Item.redstone, 'W', itemWriterPcb);

        ItemStack itemPrinter = mod.blockPeripheral.getDefaultItem(PeripheralType.PRINTER);
        manager.addShapelessRecipe(itemPrinter, itemWriter, itemPrinterPcb);

        recipes.add(new EmbeddedTagRecipe());
    }
}
