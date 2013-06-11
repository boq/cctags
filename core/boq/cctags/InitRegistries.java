package boq.cctags;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import boq.cctags.world.EntityTag;
import boq.cctags.world.ItemTag.TagSize;
import cpw.mods.fml.common.registry.EntityRegistry;

public class InitRegistries {
    private InitRegistries() {}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void registerRecipes() {
        final CCTags mod = CCTags.instance;
        final CraftingManager manager = CraftingManager.getInstance();
        List recipes = manager.getRecipeList();
        recipes.add(new DyeRecipe());

        ItemStack itemTag1K = new ItemStack(mod.itemTag, 16, TagSize.TAG_64.ordinal());
        recipes.add(new ShapedOreRecipe(itemTag1K, "PPP", "PRP", "PIP", 'P', Item.paper, 'R', Item.redstone, 'I', Item.ingotIron));

        ItemStack itemTag4K = new ItemStack(mod.itemTag, 16, TagSize.TAG_256.ordinal());
        recipes.add(new ShapedOreRecipe(itemTag4K, "PPP", "RRR", "PIP", 'P', Item.paper, 'R', Item.redstone, 'I', Item.ingotIron));

        ItemStack itemTag16K = new ItemStack(mod.itemTag, 16, TagSize.TAG_1K.ordinal());
        recipes.add(new ShapedOreRecipe(itemTag16K, "PPP", "RRR", "PGP", 'P', Item.paper, 'R', Item.redstone, 'G', Item.goldNugget));

        ItemStack itemTag64K = new ItemStack(mod.itemTag, 16, TagSize.TAG_4K.ordinal());
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
