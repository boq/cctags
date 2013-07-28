package boq.cctags.cc;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ComputerPeripheralType {
    SCANNER("tile.tag-scanner", "tag_scanner", "cctags:scanner-front", "cctags:scanner-front", TileEntityScanner.class),
    WRITER("tile.tag-writer", "tag_writer", "cctags:writer-front", "cctags:writer-front-active", TileEntityPrinter.class) {

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, List<String> info) {
            info.add(StatCollector.translateToLocal("cctag.printer-promo"));
        }

    };

    public static final ComputerPeripheralType[] TYPES = values();

    public final String unlocalizedName;
    public final String peripheralType;
    private final String inactiveIconName;
    private final String activeIconName;
    public Icon activeBlockIcon;
    public Icon inactiveBlockIcon;
    private final Class<? extends TileEntityPeripheral> teClass;

    private ComputerPeripheralType(String unlocalizedName,
            String peripheralType,
            String inactiveIconName, String activeIconName,
            Class<? extends TileEntityPeripheral> teClass) {
        this.unlocalizedName = unlocalizedName;
        this.peripheralType = peripheralType;
        this.inactiveIconName = inactiveIconName;
        this.activeIconName = activeIconName;
        this.teClass = teClass;
    }

    public TileEntityPeripheral createNewTileEntity() {
        try {
            return teClass.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        inactiveBlockIcon = registry.registerIcon(inactiveIconName);
        activeBlockIcon = registry.registerIcon(activeIconName);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info) {}
}
