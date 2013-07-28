package boq.cctags.cc;

import java.lang.reflect.Constructor;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.*;

public enum TurtlePeripheralType implements ITurtleUpgrade {
    WRITER(9255, "tile.tag_writer", "cctags:writer-turtle", "tag_writer", TurtleWriter.class),
    PRINTER(9256, "tile.tag_printer", "cctags:printer-turtle", "tag_printer", TurtlePrinter.class);

    public static final TurtlePeripheralType[] TYPES = values();

    public final int turtleId;
    public final String unlocalizedName;
    private final String turtleIconName;
    public Icon turtleIcon;
    public final String peripheralType;
    public ItemStack craftingItem;
    private final Class<? extends TurtlePeripheral> turtleClass;

    private TurtlePeripheralType(int turtleId, String unlocalizedName,
            String turtleIconName, String peripheralType,
            Class<? extends TurtlePeripheral> turtleClass) {
        this.turtleId = turtleId;
        this.unlocalizedName = unlocalizedName;
        this.turtleIconName = turtleIconName;
        this.peripheralType = peripheralType;
        this.turtleClass = turtleClass;
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
        turtleIcon = registry.registerIcon(turtleIconName);
    }

    @Override
    public int getUpgradeID() {
        return turtleId;
    }

    @Override
    public String getAdjective() {
        return LanguageRegistry.instance().getStringLocalization(unlocalizedName + ".name");
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
