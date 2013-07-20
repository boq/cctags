package boq.cctags.tag;

import java.lang.reflect.Field;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import boq.cctags.Constants;
import boq.utils.serializable.ISelectableSerializableData.IFieldSelector;
import boq.utils.serializable.SerializableField;

public class ItemTagUtils {

    public final static IFieldSelector embeddedSelector = new IFieldSelector() {
        @Override
        public boolean canVisit(Field field, int flags) {
            boolean isNBT = (flags & SerializableField.NBT_SERIALIZABLE) != 0;
            boolean notExcluded = (flags & TagData.EXCLUDE_IN_EMBEDDED_TAG) == 0;
            return isNBT && notExcluded;
        }
    };

    public final static IFieldSelector nbtOnlySelector = new IFieldSelector() {

        @Override
        public boolean canVisit(Field field, int flags) {
            boolean isNBT = (flags & SerializableField.NBT_SERIALIZABLE) != 0;
            boolean notExcluded = (flags & TagData.EXCLUDE_IN_ITEM_NBT) == 0;
            return isNBT && notExcluded;
        }
    };

    public static int calculateDamage(TagType type, TagSize size) {
        return (type.ordinal() << 8) + size.ordinal();
    }

    public static TagData createFromStack(ItemStack stack) {
        TagData data = new TagData();
        data.readFromNBT(getItemTag(stack), ItemTagUtils.nbtOnlySelector);

        data.tagSize = getSize(stack);
        data.tagType = getType(stack);
        return data;
    }

    public static int getColor(ItemStack stack) {
        final int black = ItemDye.dyeColors[0];
        return getTag(stack, TagData.TAG_COLOR, black);
    }

    public static NBTTagCompound getItemTag(ItemStack stack) {
        NBTTagCompound result = stack.getTagCompound();

        if (result == null) {
            result = new NBTTagCompound("tag");
            stack.setTagCompound(result);
        }

        return result;
    }

    public static TagSize getSize(int damage) {
        damage &= 0xFF;
        if (damage > TagSize.VALUES.length)
            return TagSize.TAG_BROKEN;

        return TagSize.VALUES[damage];
    }

    public static TagSize getSize(ItemStack stack) {
        return getSize(stack.getItemDamage());
    }

    @SuppressWarnings({ "unchecked" })
    static <T extends NBTBase> T getTag(ItemStack stack, String name) {
        NBTTagCompound tag = getItemTag(stack);
        return (T)tag.getTag(name);
    }

    public static int getTag(ItemStack stack, String name, int def) {
        NBTTagInt value = getTag(stack, name);
        return (value != null) ? value.data : def;
    }

    public static String getTag(ItemStack stack, String name, String def) {
        NBTTagString value = getTag(stack, name);
        return (value != null) ? value.data : def;
    }

    public static TagType getType(int damage) {
        damage = (damage >> 8) & 0xFF;
        if (damage > TagType.VALUES.length)
            return TagType.NORMAL;

        return TagType.VALUES[damage];
    }

    public static TagType getType(ItemStack stack) {
        return getType(stack.getItemDamage());
    }

    public static TagData readData(ItemStack stack) {
        NBTTagCompound tag = stack.stackTagCompound;
        if (tag == null)
            return null;

        TagData result = new TagData();
        result.readFromNBT(tag, ItemTagUtils.nbtOnlySelector);

        result.tagSize = getSize(stack);
        result.tagType = getType(stack);
        return result;
    }

    public static void setupDefaultTags(ItemStack stack) {
        NBTTagCompound tag = getItemTag(stack);
        tag.setInteger(TagData.TAG_COLOR, Constants.COLOR_BLACK);
    }

    public static ItemStack upgradeToType(ItemStack stack, TagType type) {
        TagSize size = getSize(stack);
        int newDamage = calculateDamage(type, size);
        ItemStack result = stack.copy();
        result.setItemDamage(newDamage);
        return result;
    }

    public static void writeData(ItemStack stack, TagData data) {
        NBTTagCompound tag = new NBTTagCompound("tag");
        data.writeToNBT(tag, ItemTagUtils.nbtOnlySelector);
        stack.stackTagCompound = tag;
    }

    public static final String EMBEDDED_TAG_PROPERTY = "CCTag";

    public static boolean hasEmbeddedTag(ItemStack is) {
        return is.hasTagCompound() && is.stackTagCompound.hasKey(EMBEDDED_TAG_PROPERTY);
    }

    public static TagData readEmbeddedData(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.stackTagCompound.getCompoundTag(EMBEDDED_TAG_PROPERTY);
            if (tag != null) {
                TagData data = new TagData();
                data.readFromNBT(tag, embeddedSelector);
                return data;
            }
        }

        return null;
    }

    public static void writeEmbeddedData(ItemStack stack, TagData data) {
        NBTTagCompound itemTag = stack.stackTagCompound;

        if (itemTag == null) {
            itemTag = new NBTTagCompound("tag");
            stack.stackTagCompound = itemTag;
        }

        NBTTagCompound dataTag = new NBTTagCompound();
        data.writeToNBT(dataTag, embeddedSelector);
        itemTag.setTag(EMBEDDED_TAG_PROPERTY, dataTag);
    }

}
