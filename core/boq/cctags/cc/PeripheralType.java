package boq.cctags.cc;

import java.lang.reflect.Constructor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.*;

public enum PeripheralType implements ITurtleUpgrade {
    WRITER(9255, "tile.tag_writer", "cctags:writer-front", "cctags:writer-front-active",
            "cctags:writer-turtle", "tag_writer", TileEntityWriter.class, TurtleWriter.class),
    PRINTER(9256, "tile.tag_printer", "cctags:printer-front", "cctags:printer-front-active",
            "cctags:printer-turtle", "tag_printer", TileEntityPrinter.class, TurtlePrinter.class);

    public static final PeripheralType[] TYPES = values();

    public final int turtleId;
    public final String unlocalizedName;
    private final String inactiveIconName;
    private final String activeIconName;
    private final String turtleIconName;
    public Icon activeBlockIcon;
    public Icon inactiveBlockIcon;
    public Icon turtleIcon;
    public final String peripheralType;
    public ItemStack craftingItem;
    private final Class<? extends TileEntityPeripheral<?>> teClass;
    private final Class<? extends TurtlePeripheral> turtleClass;

    private PeripheralType(int turtleId, String unlocalizedName,
            String inactiveIconName, String activeIconName,
            String turtleIconName, String peripheralType,
            Class<? extends TileEntityPeripheral<?>> teClass, Class<? extends TurtlePeripheral> turtleClass) {
        this.turtleId = turtleId;
        this.unlocalizedName = unlocalizedName;
        this.inactiveIconName = inactiveIconName;
        this.activeIconName = activeIconName;
        this.turtleIconName = turtleIconName;
        this.peripheralType = peripheralType;
        this.teClass = teClass;
        this.turtleClass = turtleClass;
    }

    public TileEntityPeripheral<?> createNewTileEntity() {
        try {
            return teClass.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        try {
            Constructor<? extends TurtlePeripheral> ctor = turtleClass.getConstructor(ITurtleAccess.class);
            return ctor.newInstance(turtle);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        inactiveBlockIcon = registry.registerIcon(inactiveIconName);
        activeBlockIcon = registry.registerIcon(activeIconName);
        turtleIcon = registry.registerIcon(turtleIconName);
    }

    @Override
    public int getUpgradeID() {
        return turtleId;
    }

    @Override
    public String getAdjective() {
        return StatCollector.translateToLocal(unlocalizedName + ".name");
    }

    @Override
    public ItemStack getCraftingItem() {
        return craftingItem;
    }

    @Override
    public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
        return turtleIcon;
    }

    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.Peripheral;
    }

    @Override
    public boolean isSecret() {
        return false;
    }

    @Override
    public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
        return false;
    }
}
