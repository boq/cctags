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
import boq.cctags.tag.ItemTag;
import boq.utils.misc.PlayerOrientation;
import boq.utils.misc.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTagPeripheral extends BlockContainer {

    public enum PeripheralType {
        WRITER("tile.tag_writer", "cctags:writer_front", "tag writer", TileEntityWriter.class),
        PRINTER("tile.tag_printer", "cctags:printer_front", "tag printer", TileEntityPrinter.class);

        public final String unlocalizedName;
        private final String iconName;
        public Icon icon;
        public final String peripheralType;
        private final Class<? extends TileEntityPeripheral<?>> teClass;

        private PeripheralType(String unlocalizedName, String iconName, String peripheralType, Class<? extends TileEntityPeripheral<?>> teClass) {
            this.unlocalizedName = unlocalizedName;
            this.iconName = iconName;
            this.peripheralType = peripheralType;
            this.teClass = teClass;
        }

        public TileEntityPeripheral<?> createNewTileEntity() {
            try {
                return teClass.newInstance();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    public static final PeripheralType[] TYPES = PeripheralType.values();

    public BlockTagPeripheral(int id) {
        super(id, Material.rock);
        setHardness(2.0F);
        setUnlocalizedName("tag-peripheral");
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        PeripheralType type = getType(metadata);
        return type.createNewTileEntity();
    }

    public static ForgeDirection getFront(int metadata) {
        int side = (metadata >> 2) & 3;
        return ForgeDirection.VALID_DIRECTIONS[side + 2];
    }

    public static PeripheralType getType(int metadata) {
        int type = metadata & 3;
        return TYPES[type];
    }

    public static int calculateMeta(ForgeDirection side, PeripheralType type) {
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
            PeripheralType type = getType(metadata);
            return type.icon;
        }

        return blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        blockIcon = registry.registerIcon("cctags:peripheral_top");

        for (PeripheralType type : TYPES)
            type.icon = registry.registerIcon(type.iconName);

    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
        PeripheralType type = getType(stack.getItemDamage());

        PlayerOrientation orient = PlayerOrientation.getEntityOrientation(player);
        ForgeDirection side = orient.toDirection().getOpposite();

        int meta = calculateMeta(side, type);
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List result) {
        for (PeripheralType type : TYPES) {
            int meta = calculateMeta(ForgeDirection.SOUTH, type);
            result.add(new ItemStack(id, 1, meta));
        }
    }

    @Override
    public int damageDropped(int metadata) {
        PeripheralType type = getType(metadata);
        return calculateMeta(ForgeDirection.SOUTH, type);
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

        boolean used = false;

        if (te instanceof TileEntityPrinter)
            used = ((TileEntityPrinter)te).addInk(equipped);

        if (!used && te instanceof TileEntityPeripheral) {
            TileEntityPeripheral<?> tep = (TileEntityPeripheral<?>)te;

            if (isItemTag(equipped))
                used = tep.insertTag(equipped);
            else
                return tep.ejectTag();
        }

        if (used) {
            equipped.stackSize--;

            if (equipped.stackSize <= 0)
                player.destroyCurrentEquippedItem();
        }

        return used;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int metadata) {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te instanceof TileEntityPeripheral<?>) {
            ItemStack is = ((TileEntityPeripheral<?>)te).getDroppedItem();
            
            if (is != null)
                Utils.dropItem(world, x, y, z, is);
        }

        super.breakBlock(world, x, y, z, blockId, metadata);
    }

}
