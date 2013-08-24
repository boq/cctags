package boq.cctags.tag;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.*;
import net.minecraft.world.World;
import boq.utils.misc.Utils;

public class EmbeddedTagRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        boolean foundTag = false;
        boolean foundItem = false;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack is = inventory.getStackInSlot(i);

            if (is == null)
                continue;

            if (is.getItem() instanceof ItemTag)
                foundTag = true;
            else if (!foundItem && !ItemTagUtils.hasEmbeddedTag(is))
                foundItem = true;
            else
                return false;
        }

        return foundItem && foundTag;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack tag = null;
        ItemStack item = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack is = inventory.getStackInSlot(i);

            if (is == null)
                continue;

            if (is.getItem() instanceof ItemTag)
                tag = is;
            else if (!ItemTagUtils.hasEmbeddedTag(is))
                item = is;
            else
                return null;

            if (tag != null && item != null)
                break;
        }

        if (tag == null || item == null)
            return null;

        TagData data = ItemTagUtils.readData(tag);
        ItemStack result = item.copy();

        ItemTagUtils.writeEmbeddedData(result, data);

        markAsTagged(result);

        return result;
    }

    private static void markAsTagged(ItemStack result) { // right now there is no better way, I guess
        NBTTagCompound tagItem = result.getTagCompound();
        NBTTagCompound display = Utils.getTag(tagItem, "display");
        if (display == null) {
            display = new NBTTagCompound();
            tagItem.setTag("display", display);
        }

        NBTTagList lore = Utils.getTag(display, "Lore");
        if (lore == null) {
            lore = new NBTTagList();
            display.setTag("Lore", lore);
        }

        lore.appendTag(new NBTTagString("", "Tagged"));
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
