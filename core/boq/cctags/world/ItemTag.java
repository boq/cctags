package boq.cctags.world;

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
import boq.cctags.Constants;
import boq.cctags.client.TagIcons;
import boq.utils.misc.PlayerOrientation;
import boq.utils.misc.Rotation;
import boq.utils.serializable.ISelectableSerializableData.IFieldSelector;
import boq.utils.serializable.SerializableField;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTag extends Item {

    public static final TagSize[] sizes = TagSize.values();

    private Icon backgroundIcon;

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
        tag.setInteger(TagData.TAG_COLOR, Constants.COLOR_GREEN);
        tag.setString(TagData.TAG_ICON, Constants.DEFAULT_ICON);
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
        if (damage > sizes.length)
            return TagSize.TAG_BROKEN;

        return sizes[damage];
    }

    public static TagSize getSize(ItemStack stack) {
        return getSize(stack.getItemDamage());
    }

    public static int getColor(ItemStack stack) {
        final int white = ItemDye.dyeColors[15];
        return getTag(stack, TagData.TAG_COLOR, white);
    }

    public ItemTag(int par1) {
        super(par1);
        setUnlocalizedName("cctag");
        setCreativeTab(CreativeTabs.tabDecorations);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:tag-marker");
        backgroundIcon = registry.registerIcon("cctags:tag-background");

        for (TagSize size : sizes)
            size.icon = registry.registerIcon(size.iconId);

        TagIcons.instance.registerIcons(registry);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List description, boolean extended) {
        TagSize size = getSize(stack);
        String name = getTag(stack, "Name", null);

        final LanguageRegistry reg = LanguageRegistry.instance();
        if (name != null) {
            String nameTemplate = reg.getStringLocalization("cctag.name");
            description.add(String.format(nameTemplate, "name"));
        }

        String sizeTemplate = reg.getStringLocalization("cctag.size");
        description.add(String.format(sizeTemplate, size.name));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List results) {
        for (TagSize size : sizes)
            if (size.visible) {
                ItemStack stack = new ItemStack(id, 1, size.ordinal());
                setupDefaultTags(stack);
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
                return backgroundIcon;
            case 2:
                return getSize(damage).icon;
            default:
                return itemIcon;

        }
    }

    private final static IFieldSelector createSelector = new IFieldSelector() {

        @Override
        public boolean canVisit(Field field, int flags) {
            boolean isNBT = (flags & SerializableField.NBT_SERIALIZABLE) != 0;
            boolean notExcluded = (flags & TagData.EXCLUDE_FROM_INITIAL) == 0;
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
        data.readFromNBT(getItemTag(stack), createSelector);

        data.tagSize = getSize(stack);

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
        return true;
    }
}
