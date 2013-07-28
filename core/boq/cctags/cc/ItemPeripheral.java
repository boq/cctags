package boq.cctags.cc;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        ComputerPeripheralType type = BlockTagPeripheral.getType(stack.getItemDamage());
        return type.unlocalizedName;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean expanded) {
        ComputerPeripheralType type = BlockTagPeripheral.getType(stack.getItemDamage());
        type.addInformation(stack, list);
        super.addInformation(stack, player, list, expanded);
    }

}
