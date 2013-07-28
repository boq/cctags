package boq.cctags.cc;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import boq.cctags.CCTags;
import boq.cctags.Recipes;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMisc extends Item {

    private final Map<Integer, SubItem> types = Maps.newHashMap();

    public class SubItem {
        private final String iconName;
        private Icon icon;
        public final String unlocalizedName;
        public final int id;

        private SubItem(int id, String unlocalizedName, String iconName) {
            this.id = id;
            this.unlocalizedName = unlocalizedName;
            this.iconName = iconName;
            types.put(id, this);
        }

        public void addCreativeItems(int itemId, List<ItemStack> list) {
            list.add(getStack());
        }

        public void registerIcons(IconRegister registry) {
            icon = registry.registerIcon(iconName);
        }

        public Icon icon() {
            return icon;
        }

        public ItemStack getStack() {
            return new ItemStack(ItemMisc.this, 1, id);
        }

        public boolean checkItem(ItemStack stack) {
            return stack != null &&
                    stack.getItem() instanceof ItemMisc &&
                    stack.getItemDamage() == id;
        }
    }

    private class TurtlePeripheralItem extends SubItem {
        private final TurtlePeripheralType type;
        private final boolean addTurtle;

        private TurtlePeripheralItem(TurtlePeripheralType type, int id, String name, String iconName, boolean addTurtle) {
            super(id, name, iconName);
            this.type = type;
            this.addTurtle = addTurtle;
        }

        @Override
        public void addCreativeItems(int itemId, List<ItemStack> list) {

            list.add(new ItemStack(itemId, 1, id));
            if (addTurtle)
                Recipes.addUpgradedTurtles(list, type);
        }
    }

    public final SubItem WRITER_PCB = new TurtlePeripheralItem(TurtlePeripheralType.WRITER_OLD, 0, "item.pcb-writer", "cctags:pcb-writer", false);
    public final SubItem PRINTER_PCB = new TurtlePeripheralItem(TurtlePeripheralType.WRITER, 1, "item.pcb-printer", "cctags:pcb-printer", true);

    public final SubItem HANDHELD_OLD = new SubItem(2, "item.tag-reader.old", "cctags:handheld") {
        @Override
        public void addCreativeItems(int itemId, List<ItemStack> list) {}
    };

    public final SubItem ANTENA_UPGRADE = new SubItem(3, "item.pcb-antenna", "cctags:pcb-antenna");

    public ItemMisc(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CCTags.instance.tabTags);
        setUnlocalizedName("cctags-misc");
    }

    @Override
    public Icon getIconFromDamage(int damage) {
        SubItem t = types.get(damage);
        return t != null ? t.icon() : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        itemIcon = registry.registerIcon("cctags:broken-item");
        for (SubItem s : types.values())
            s.registerIcons(registry);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        SubItem t = types.get(stack.getItemDamage());
        return t != null ? t.unlocalizedName : "broken!";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(int id, CreativeTabs tab, List result) {
        for (SubItem s : types.values())
            s.addCreativeItems(id, result);
    }
}
