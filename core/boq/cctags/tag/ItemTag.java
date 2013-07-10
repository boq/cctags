package boq.cctags.tag;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.*;
import boq.cctags.LuaInit.LibEntry;
import boq.utils.misc.PlayerOrientation;
import boq.utils.misc.Rotation;
import boq.utils.serializable.ISelectableSerializableData.IFieldSelector;
import boq.utils.serializable.SerializableField;

import com.google.common.base.Strings;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTag extends Item {

    private final static String CATEGORY_TAG = "Category";
    private final static String COMMENT_TAG = "Comment";

    public static NBTTagCompound getItemTag(ItemStack stack) {
        NBTTagCompound result = stack.getTagCompound();

        if (result == null) {
            result = new NBTTagCompound("tag");
            stack.setTagCompound(result);
        }

        return result;
    }

    public static void setupDefaultTags(ItemStack stack) {
        NBTTagCompound tag = getItemTag(stack);
        tag.setInteger(TagData.TAG_COLOR, Constants.COLOR_BLACK);
        tag.setInteger(TagData.TAG_SERIAL, -1);
    }

    @SuppressWarnings({ "unchecked" })
    private static <T extends NBTBase> T getTag(ItemStack stack, String name) {
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

    public static TagSize getSize(int damage) {
        damage &= 0xFF;
        if (damage > TagSize.VALUES.length)
            return TagSize.TAG_BROKEN;

        return TagSize.VALUES[damage];
    }

    public static TagSize getSize(ItemStack stack) {
        return getSize(stack.getItemDamage());
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

    public static int calculateDamage(TagType type, TagSize size) {
        return (type.ordinal() << 8) + size.ordinal();
    }

    public static int getColor(ItemStack stack) {
        final int black = ItemDye.dyeColors[0];
        return getTag(stack, TagData.TAG_COLOR, black);
    }

    public static TagData readData(ItemStack stack) {
        NBTTagCompound tag = stack.stackTagCompound;
        if (tag == null)
            return null;

        TagData result = new TagData();
        result.readFromNBT(tag, nbtOnlySelector);

        result.tagSize = getSize(stack);
        result.tagType = getType(stack);
        return result;
    }

    public static void writeData(ItemStack stack, TagData data) {
        NBTTagCompound tag = new NBTTagCompound("tag");
        data.writeToNBT(tag, nbtOnlySelector);
        stack.stackTagCompound = tag;
    }

    public ItemStack createFromData(TagData data) {
        int damage = calculateDamage(data.tagType, data.tagSize);
        ItemStack result = new ItemStack(this, 1, damage);
        writeData(result, data);
        return result;
    }

    public ItemTag(int par1) {
        super(par1);
        setUnlocalizedName("cctag");
        setCreativeTab(CCTags.instance.tabTags);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:tag-marker");
        for (TagType type : TagType.VALUES)
            type.registerIcons(registry);

        for (TagSize size : TagSize.VALUES)
            size.registerIcons(registry);

        TagIcons.instance.registerIcons(registry);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List description, boolean extended) {
        TagSize size = getSize(stack);

        final LanguageRegistry reg = LanguageRegistry.instance();
        String sizeTemplate = reg.getStringLocalization("cctag.size");
        description.add(String.format(sizeTemplate, size.name));

        NBTTagCompound tag = getItemTag(stack);
        if (tag.hasKey(CATEGORY_TAG)) {
            String category = "cctag.category." + tag.getString(CATEGORY_TAG);
            description.add(String.format(
                    reg.getStringLocalization("cctag.category"),
                    reg.getStringLocalization(category)
                    ));
        }

        if (tag.hasKey(COMMENT_TAG))
            description.add("\u00A7o" + tag.getString(COMMENT_TAG) + "\u00A7r");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getType(stack).unlocalizedName;
    }

    @Override
    public String getItemDisplayName(ItemStack stack) {
        String label = getTag(stack, TagData.TAG_LABEL, null);

        if (Strings.isNullOrEmpty(label))
            return super.getItemDisplayName(stack);

        return label;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List results) {
        for (TagType type : TagType.VALUES)
            if (type.visible)
                for (TagSize size : TagSize.VALUES)
                    if (size.visible()) {
                        ItemStack stack = new ItemStack(id, 1, calculateDamage(type, size));
                        setupDefaultTags(stack);
                        results.add(stack);
                    }

        for (LibEntry e : LuaInit.instance.getLibrary().values()) {
            ItemStack stack = new ItemStack(id, 1, e.size.ordinal());
            TagData data = new TagData();
            data.color = e.color;
            data.serialId = -1;
            data.contents = e.contents;
            data.label = e.label;
            data.icon = e.icon;

            NBTTagCompound tag = getItemTag(stack);
            data.writeToNBT(tag, nbtOnlySelector);

            if (e.category != null)
                tag.setString(CATEGORY_TAG, e.category);

            if (e.comment != null)
                tag.setString(COMMENT_TAG, e.comment);

            results.add(stack);
        }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return (renderPass == 1) ? getColor(stack) : Constants.COLOR_WHITE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        switch (pass) {
            case 0:
                return getType(damage).backgroundIcon;
            case 2:
                return getSize(damage).icon;
            default:
                return itemIcon;

        }
    }

    public final static IFieldSelector nbtOnlySelector = new IFieldSelector() {

        @Override
        public boolean canVisit(Field field, int flags) {
            boolean isNBT = (flags & SerializableField.NBT_SERIALIZABLE) != 0;
            boolean notExcluded = (flags & TagData.EXCLUDE_IN_ITEM_NBT) == 0;
            return isNBT && notExcluded;
        }
    };

    public boolean isBlockSideAvailable(ForgeDirection dir, World world, int x, int y, int z) {
        for (EntityTag e : TagUtils.getBlockTags(world, x, y, z))
            if (e.data.side == dir)
                return false;

        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (world.isRemote || !player.canPlayerEdit(x, y, z, side, stack))
            return false;

        TagData data = new TagData();
        data.readFromNBT(getItemTag(stack), nbtOnlySelector);

        data.tagSize = getSize(stack);
        data.tagType = getType(stack);
        data.side = ForgeDirection.VALID_DIRECTIONS[side];

        if (!isBlockSideAvailable(data.side, world, x, y, z))
            return false;

        switch (data.side) {
            case UP: {
                PlayerOrientation orientation = PlayerOrientation.getEntityOrientation(player);
                data.rotation = Rotation.playerToSwitchOrientation(orientation, false);
                break;
            }

            case DOWN: {
                PlayerOrientation orientation = PlayerOrientation.getEntityOrientation(player);
                data.rotation = Rotation.playerToSwitchOrientation(orientation, true).opposite();
                break;
            }

            default:
                data.rotation = Rotation.R0;
        }

        EntityTag tag = new EntityTag(world, data);
        tag.setPosition(x + 0.5, y + 0.5, z + 0.5);

        world.spawnEntityInWorld(tag);

        stack.stackSize--;
        return true;
    }

    public static ItemStack upgradeToType(ItemStack stack, TagType type) {
        TagSize size = getSize(stack);
        int newDamage = calculateDamage(type, size);
        ItemStack result = stack.copy();
        result.setItemDamage(newDamage);
        return result;
    }
}
