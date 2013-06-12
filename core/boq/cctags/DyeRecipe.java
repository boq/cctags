package boq.cctags;

import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import boq.cctags.world.ItemTag;

import com.google.common.collect.Maps;

public class DyeRecipe implements IRecipe {

    private final static String[] dyeNames =
    {
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"
    };

    public final Map<Integer, Integer> dyes = Maps.newHashMap();

    public DyeRecipe() {
        for (int i = 0; i < dyeNames.length; i++) {
            String dyeOreName = dyeNames[i];
            int dyeOreId = OreDictionary.getOreID(dyeOreName);
            int dyeColor = ItemDye.dyeColors[i];
            dyes.put(dyeOreId, dyeColor);
        }
    }

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
            else if (OreDictionary.getOreID(stack) != -1)
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
                int oreId = OreDictionary.getOreID(stack);
                Integer oreColor = dyes.get(oreId);
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
        NBTTagCompound itemTag = ItemTag.getItemTag(newItem);
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
