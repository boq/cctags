package boq.cctags.world;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import boq.cctags.Constants;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTag extends Item {

    public static final String TAG_COLOR = "Color";

    public final static int[] DEFAULT_SIZES = { 1, 4, 16, 64 };

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

    public static int getSizeInK(ItemStack stack) {
        return stack.getItemDamage();
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
        itemIcon = registry.registerIcon("cctags:tag");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return getTag(stack, TAG_COLOR, Constants.COLOR_WHITE);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List description, boolean extended) {
        int size = getSizeInK(stack);
        String name = getTag(stack, "Name", null);

        final LanguageRegistry reg = LanguageRegistry.instance();
        if (name != null) {
            String nameTemplate = reg.getStringLocalization("cctag.name");
            description.add(String.format(nameTemplate, "name"));
        }

        String sizeTemplate = reg.getStringLocalization("cctag.size");
        description.add(String.format(sizeTemplate, size));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List results) {
        for (int size : DEFAULT_SIZES)
            results.add(new ItemStack(id, 1, size));
    }

    public Icon getIcon(String id) {
        return itemIcon;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (!player.canPlayerEdit(x, y, z, side, stack))
            return false;

        EntityTag tag = new EntityTag(world);

        tag.posX = x;
        tag.posY = y;
        tag.posZ = z;

        world.spawnEntityInWorld(tag);

        return true;
    }

}
