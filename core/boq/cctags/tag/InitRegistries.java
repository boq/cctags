package boq.cctags.tag;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import boq.cctags.CCTags;
import boq.cctags.Constants;
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
        recipes.add(new ShapedOreRecipe(itemTag64, "PPP", "PRP", "PIP", 'P', Item.paper, 'R', Item.redstone, 'I', Item.ingotIron));

        ItemStack itemTag256 = new ItemStack(mod.itemTag, 16, TagSize.TAG_256.ordinal());
        ItemTag.setupDefaultTags(itemTag256);
        recipes.add(new ShapedOreRecipe(itemTag256, "PPP", "RRR", "PIP", 'P', Item.paper, 'R', Item.redstone, 'I', Item.ingotIron));

        ItemStack itemTag1K = new ItemStack(mod.itemTag, 16, TagSize.TAG_1K.ordinal());
        ItemTag.setupDefaultTags(itemTag1K);
        recipes.add(new ShapedOreRecipe(itemTag1K, "PPP", "RRR", "PGP", 'P', Item.paper, 'R', Item.redstone, 'G', Item.goldNugget));

        ItemStack itemTag64K = new ItemStack(mod.itemTag, 16, TagSize.TAG_4K.ordinal());
        ItemTag.setupDefaultTags(itemTag64K);
        recipes.add(new ShapedOreRecipe(itemTag64K, "PPP", "RRR", "PGP", 'P', Item.paper, 'R', Item.redstone, 'G', Item.ingotGold));
    }

    private static void registerEntities() {
        final CCTags mod = CCTags.instance;
        EntityRegistry.registerModEntity(EntityTag.class, "CCTag", Constants.ENTITY_TAG, mod, 50, Integer.MAX_VALUE, false);
    }

    public static void registerAllTheThings() {
        registerRecipes();
        registerEntities();
    }
}
