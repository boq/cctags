package boq.cctags.world;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTag extends Item {

    public static final String TAG_COLOR = "Color";

    public enum TagSize {
        TAG_BROKEN(false, "cctags:tag-broken", 0, "\u00A7kKAPUT\u00A7r"),
        TAG_4G(true, "cctags:tag-inf", -1, "Almost unlimited"),
        TAG_64(true, "cctags:tag-64", 64, "64"),
        TAG_256(true, "cctags:tag-256", 256, "256"),
        TAG_1K(true, "cctags:tag-1k", 1024, "1k"),
        TAG_4K(true, "cctags:tag-4k", 4096, "4k");

        public final boolean visible;
        public final String iconId;
        private Icon icon;

        public final int size;
        public final String name;

        private TagSize(boolean visible, String iconId, int size, String name) {
            this.visible = visible;
            this.iconId = iconId;
            this.size = size;
            this.name = name;
        }
    }

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
            if (size.visible)
                results.add(new ItemStack(id, 1, size.ordinal()));
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
        final int white = ItemDye.dyeColors[15];
        return (renderPass == 1) ? getTag(stack, TAG_COLOR, white) : white;
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

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (!player.canPlayerEdit(x, y, z, side, stack) || world.isRemote)
            return false;

        EntityTag tag = new EntityTag(world);

        tag.posX = x;
        tag.posY = y;
        tag.posZ = z;

        world.spawnEntityInWorld(tag);

        return true;
    }

}
