package boq.cctags.cc;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemPeripheral extends ItemBlock {

    public ItemPeripheral(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public Icon getIconFromDamage(int damage) {
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        PeripheralType type = BlockTagPeripheral.getType(stack.getItemDamage());
        return type.unlocalizedName;
    }

}
