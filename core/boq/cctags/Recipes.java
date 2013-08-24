package boq.cctags;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;
import boq.cctags.cc.ComputerPeripheralType;
import boq.cctags.cc.TurtlePeripheralType;
import boq.cctags.tag.*;

import com.google.common.collect.ObjectArrays;

import cpw.mods.fml.common.registry.GameRegistry;
import dan200.turtle.api.ITurtleUpgrade;

public class Recipes {
    private static final int NUMBER_OF_TURTLE_TOOLS = 7;

    private Recipes() {}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static ItemStack createTagStack(List recipes, ItemTag item, int count, TagSize size) {
        int damage = ItemTagUtils.calculateDamage(TagType.NORMAL, size);
        ItemStack result = new ItemStack(item, count, damage);
        ItemTagUtils.setupDefaultTags(result);

        recipes.add(new BigTagRecipe(result));
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

        int damage = ItemTagUtils.calculateDamage(TagType.NORMAL, TagSize.TAG_4G);
        ItemStack infTag = new ItemStack(mod.itemTag, 1, damage);
        ItemTagUtils.setupDefaultTags(infTag);
        recipes.add(new BigTagRecipe(infTag));

        ItemStack itemHandheld = new ItemStack(mod.itemReader);
        ItemStack itemHandheldAdvanced = new ItemStack(mod.itemReader, 1, ItemReader.READER_ADVANCED);
        recipes.add(new ShapedOreRecipe(itemHandheld, "WRW", "IDG", "WRW", 'W', "plankWood", 'R', Item.redstone, 'G', Block.thinGlass, 'D', "dyeGreen", 'I', Item.ingotIron));

        ItemStack itemManipulator = new ItemStack(mod.itemManipulator);
        manager.addRecipe(itemManipulator, "SI", "RS", 'S', Item.stick, 'R', Item.redstone, 'I', Item.ingotIron);

        ItemStack itemHandheldOld = mod.itemMisc.HANDHELD_OLD.getStack();
        manager.addShapelessRecipe(itemHandheld, itemHandheldOld);

        ItemStack itemWriterPcb = mod.itemMisc.WRITER_PCB.getStack();
        manager.addRecipe(itemWriterPcb, "GGG", " R ", " I ", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron);
        TurtlePeripheralType.WRITER_OLD.craftingItem = itemWriterPcb;

        ItemStack itemPrinterPcb = mod.itemMisc.PRINTER_PCB.getStack();
        manager.addRecipe(itemPrinterPcb, "GGG", "RRR", "IBI", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron, 'B', new ItemStack(Item.dyePowder, 1, 0));
        TurtlePeripheralType.WRITER.craftingItem = itemPrinterPcb;

        ItemStack itemAntennaPcb = mod.itemMisc.ANTENA_UPGRADE.getStack();
        manager.addRecipe(itemAntennaPcb, "GGG", "IRI", " E ", 'G', Item.goldNugget, 'R', Item.redstone, 'I', Item.ingotIron, 'E', Item.enderPearl);

        ItemStack itemPrinter = mod.blockPeripheral.getDefaultItem(ComputerPeripheralType.WRITER);
        manager.addRecipe(itemPrinter, "SSS", "WRP", "SSS", 'S', Block.stone, 'R', Item.redstone, 'W', itemWriterPcb, 'P', itemPrinterPcb);

        ItemStack itemScanner = mod.blockPeripheral.getDefaultItem(ComputerPeripheralType.SCANNER);
        manager.addRecipe(itemScanner, "SSS", "WRA", "SSS", 'S', Block.stone, 'R', Item.redstone, 'W', itemWriterPcb, 'A', itemAntennaPcb);

        manager.addShapelessRecipe(itemHandheldAdvanced, itemHandheld, itemAntennaPcb);

        recipes.add(new EmbeddedTagRecipe());
    }

    public static ItemStack getExpandedTurtleItemStack() {
        return GameRegistry.findItemStack("CCTurtle", "CC-TurtleExpanded", 1);
    }

    public static ItemStack createTurtleItemStack(ITurtleUpgrade left, ITurtleUpgrade right) {
        return createTurtleItemStack(
                left == null ? null : (short)left.getUpgradeID(),
                right == null ? null : (short)right.getUpgradeID());
    }

    public static ItemStack createTurtleItemStack(Short left, Short right) {
        ItemStack turtle = getExpandedTurtleItemStack();

        if (turtle == null)
            return null;

        NBTTagCompound tag = turtle.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            turtle.setTagCompound(tag);
        }

        if (left != null)
            tag.setShort("leftUpgrade", left);

        if (right != null)
            tag.setShort("rightUpgrade", right);

        return turtle;
    }

    public static void addUpgradedTurtles(List<ItemStack> result, ITurtleUpgrade upgrade) {
        if (CCTags.config.ADD_OWN_TURTLES)
            for (int i = 0; i < NUMBER_OF_TURTLE_TOOLS; i++)
                result.add(createTurtleItemStack((short)upgrade.getUpgradeID(), (short)i));
    }
}
