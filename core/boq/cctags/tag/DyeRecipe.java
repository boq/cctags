package boq.cctags.tag;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import boq.utils.misc.Dyes;
import boq.utils.misc.Utils;

public class DyeRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        boolean hasDye = false;
        boolean hasTag = false;

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null)
                continue;

            if (stack.getItem() instanceof ItemTag)
                hasTag = true;
            else if (Dyes.isDye(stack))
                hasDye = true;
            else
                break;

            if (hasDye && hasTag)
                return true;
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack tag = null;
        int dyeColor = -1;

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null)
                continue;

            if (stack.getItem() instanceof ItemTag)
                tag = stack;
            else {
                Integer oreColor = Dyes.dyeStackToColor(stack);
                if (oreColor != null)
                    dyeColor = oreColor;
            }

            if (tag != null && dyeColor != -1)
                return createTag(tag, dyeColor);
        }

        return null;
    }

    private static ItemStack createTag(ItemStack item, int dyeOreId) {
        ItemStack newItem = item.copy();
        newItem.stackSize = 1;
        NBTTagCompound itemTag = Utils.getItemTag(newItem);
        itemTag.setInteger(TagData.TAG_COLOR, dyeOreId);
        return newItem;
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
