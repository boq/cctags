package boq.cctags.cc;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ComputerPeripheralType {
    WRITER("tile.tag_writer", "tag-writer", "cctags:writer-front", "cctags:writer-front-active", TileEntityWriter.class),
    PRINTER("tile.tag_printer", "tag-writer", "cctags:printer-front", "cctags:printer-front-active", TileEntityPrinter.class);

    public static final ComputerPeripheralType[] TYPES = values();

    public final String unlocalizedName;
    public final String peripheralType;
    private final String inactiveIconName;
    private final String activeIconName;
    public Icon activeBlockIcon;
    public Icon inactiveBlockIcon;
    private final Class<? extends TileEntityPeripheral<?>> teClass;

    private ComputerPeripheralType(String unlocalizedName,
            String peripheralType,
            String inactiveIconName, String activeIconName,
            Class<? extends TileEntityPeripheral<?>> teClass) {
        this.unlocalizedName = unlocalizedName;
        this.peripheralType = peripheralType;
        this.inactiveIconName = inactiveIconName;
        this.activeIconName = activeIconName;
        this.teClass = teClass;
    }

    public TileEntityPeripheral<?> createNewTileEntity() {
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
}
