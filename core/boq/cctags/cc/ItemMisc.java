package boq.cctags.cc;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMisc extends Item {

    public enum Subtype {
        WRITER_PCB("item.pcb-writer", "cctags:pcb-writer"),
        PRINTER_PCB("item.pcb-printer", "cctags:pcb-printer");
        public final String name;
        public final String iconName;
        private Icon icon;

        private Subtype(String name, String iconName) {
            this.name = name;
            this.iconName = iconName;
        }
    }

    private Subtype[] types = Subtype.values();

    private Subtype getSubtype(int id) {
        try {
            return types[id];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public ItemMisc(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public Icon getIconFromDamage(int damage) {
        Subtype t = getSubtype(damage);

        return t != null ? t.icon : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:broken-item");
        for (Subtype s : types)
            s.icon = registry.registerIcon(s.iconName);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Subtype t = getSubtype(stack.getItemDamage());
        return t != null ? t.name : "broken!";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(int id, CreativeTabs tab, List result) {
        for (Subtype s : types)
            result.add(new ItemStack(id, 1, s.ordinal()));
    }
}
