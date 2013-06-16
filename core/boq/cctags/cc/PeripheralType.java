package boq.cctags.cc;

import java.lang.reflect.Constructor;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.*;

public enum PeripheralType implements ITurtleUpgrade {
    WRITER(9255, "tile.tag_writer", "cctags:writer_front", "cctags:writer_turtle", "tag_writer", new ItemStack(Item.redstone), TileEntityWriter.class,
            TurtleWriter.class),
    PRINTER(9256, "tile.tag_printer", "cctags:printer_front", "cctags:writer_turtle", "tag_printer", new ItemStack(Item.goldNugget), TileEntityPrinter.class,
            TurtlePrinter.class);

    public static final PeripheralType[] TYPES = values();

    public final int turtleId;
    public final String unlocalizedName;
    private final String blockIconName;
    private final String turtleIconName;
    public Icon blockIcon;
    public Icon turtleIcon;
    public final String peripheralType;
    public final ItemStack craftingItem;
    private final Class<? extends TileEntityPeripheral<?>> teClass;
    private final Class<? extends TurtlePeripheral> turtleClass;

    private PeripheralType(int turtleId, String unlocalizedName, String blockIconName, String turtleIconName, String peripheralType, ItemStack craftingItem,
            Class<? extends TileEntityPeripheral<?>> teClass, Class<? extends TurtlePeripheral> turtleClass) {
        this.turtleId = turtleId;
        this.unlocalizedName = unlocalizedName;
        this.blockIconName = blockIconName;
        this.turtleIconName = turtleIconName;
        this.peripheralType = peripheralType;
        this.craftingItem = craftingItem;
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

    public void registerIcons(IconRegister registry) {
        blockIcon = registry.registerIcon(blockIconName);
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
