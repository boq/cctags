package boq.cctags.tag;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class BigTagRecipe extends ShapedRecipes {

    static ItemStack[] bigTagPattern(ItemStack center) {
        final ItemStack slime = new ItemStack(Item.slimeBall);
        final ItemStack paper = new ItemStack(Item.paper);
        center = center.copy();

        return new ItemStack[] {
                paper, slime, paper,
                paper, center, paper,
                paper, paper, paper
        };
    }

    private final TagSize size;

    public BigTagRecipe(ItemStack smallTag) {
        super(3, 3, bigTagPattern(smallTag), ItemTagUtils.upgradeToType(smallTag, TagType.BIG));
        size = ItemTagUtils.getSize(smallTag);
    }

    private static boolean isItemEqual(ItemStack expected, ItemStack received) {
        return received == null ? false : expected.isItemEqual(received);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        final ItemStack slime = new ItemStack(Item.slimeBall);
        final ItemStack paper = new ItemStack(Item.paper);
        for (int row = 0; row < 3; row++)
            if (!isItemEqual(paper, inventory.getStackInRowAndColumn(0, row)) ||
                    !isItemEqual(paper, inventory.getStackInRowAndColumn(2, row)))
                return false;

        if (!isItemEqual(paper, inventory.getStackInRowAndColumn(1, 2)))
            return false;

        if (!isItemEqual(slime, inventory.getStackInRowAndColumn(1, 0)))
            return false;

        ItemStack tag = inventory.getStackInRowAndColumn(1, 1);

        return tag != null &&
                tag.getItem() instanceof ItemTag &&
                ItemTagUtils.getSize(tag) == size &&
                ItemTagUtils.getType(tag) == TagType.NORMAL;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack center = inv.getStackInRowAndColumn(1, 1);
        ItemStack result = ItemTagUtils.upgradeToType(center, TagType.BIG);
        result.stackSize = 1;
        return result;
    }
}
