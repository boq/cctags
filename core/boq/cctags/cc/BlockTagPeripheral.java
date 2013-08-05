package boq.cctags.cc;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.CCTags;
import boq.cctags.tag.ItemTag;
import boq.utils.misc.PlayerOrientation;
import boq.utils.misc.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTagPeripheral extends BlockContainer {

    public BlockTagPeripheral(int id) {
        super(id, Material.rock);
        setHardness(2.0F);
        setUnlocalizedName("tag-peripheral");
        setCreativeTab(CCTags.instance.tabTags);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        ComputerPeripheralType type = getType(metadata);
        return type.createNewTileEntity();
    }

    public static ForgeDirection getFront(int metadata) {
        int side = (metadata >> 2) & 3;
        return ForgeDirection.VALID_DIRECTIONS[side + 2];
    }

    public static ComputerPeripheralType getType(int metadata) {
        int type = metadata & 1;
        return ComputerPeripheralType.TYPES[type];
    }

    public static boolean isActive(int metadata) {
        return (metadata & 2) > 0;
    }

    public static int setActive(int metadata) {
        return metadata | 2;
    }

    public static int clearActive(int metadata) {
        return metadata & ~2;
    }

    public static int calculateMeta(ForgeDirection side, ComputerPeripheralType type) {
        int typePart = type.ordinal() & 3;
        int sidePart = (side.ordinal() - 2) & 3;
        return (sidePart << 2) | typePart;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int sideId, int metadata) {
        ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[sideId];
        ForgeDirection front = getFront(metadata);

        if (side == front) {
            ComputerPeripheralType type = getType(metadata);
            return isActive(metadata) ? type.activeBlockIcon : type.inactiveBlockIcon;
        }

        return blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        blockIcon = registry.registerIcon("cctags:peripheral-top");

        for (ComputerPeripheralType type : ComputerPeripheralType.TYPES)
            type.registerIcons(registry);

        for (TurtlePeripheralType type : TurtlePeripheralType.TYPES)
            type.registerIcons(registry);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
        ComputerPeripheralType type = getType(stack.getItemDamage());

        PlayerOrientation orient = PlayerOrientation.getEntityOrientation(player);
        ForgeDirection side = orient.toDirection().getOpposite();

        int meta = calculateMeta(side, type);
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List result) {
        for (ComputerPeripheralType type : ComputerPeripheralType.TYPES)
            result.add(getDefaultItem(type));
    }

    @Override
    public int damageDropped(int metadata) {
        ComputerPeripheralType type = getType(metadata);
        return calculateMeta(ForgeDirection.WEST, type);
    }

    private static boolean isItemTag(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemTag;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float offsetX, float offsetY, float offsetZ) {
        if (world.isRemote)
            return false;

        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te == null)
            return false;

        ItemStack equipped = player.getCurrentEquippedItem();

        if (te instanceof TileEntityPrinter) {
            boolean used = ((TileEntityPrinter)te).addInk(equipped);

            if (!used) {
                TileEntityPrinter tep = (TileEntityPrinter)te;

                if (isItemTag(equipped))
                    used = tep.insertTag(equipped);
                else if (player.isSneaking() && equipped == null)
                    tep.giveTagToPlayer(player, true);
                else
                    return tep.ejectTag(true);
            }

            if (used) {
                equipped.stackSize--;

                if (equipped.stackSize <= 0)
                    player.destroyCurrentEquippedItem();

                return true;
            }
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int metadata) {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te instanceof TileEntityPrinter) {
            ItemStack is = ((TileEntityPrinter)te).getDroppedItem();

            if (is != null)
                Utils.dropItem(world, x, y, z, is);
        }

        super.breakBlock(world, x, y, z, blockId, metadata);
    }

    public ItemStack getDefaultItem(ComputerPeripheralType type) {
        int meta = calculateMeta(ForgeDirection.WEST, type);
        return new ItemStack(this, 1, meta);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return isActive(meta) ? 15 : 0;
    }
}
