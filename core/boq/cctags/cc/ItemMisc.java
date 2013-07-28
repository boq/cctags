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

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMisc extends Item {

    public static abstract class SubItem {
        private final String iconName;
        private Icon icon;
        public final String unlocalizedName;
        public final int id;

        private SubItem(int id, String unlocalizedName, String iconName) {
            this.id = id;
            this.unlocalizedName = unlocalizedName;
            this.iconName = iconName;
        }

        public abstract void addCreativeItems(int itemId, List<ItemStack> list);

        public void registerIcons(IconRegister registry) {
            icon = registry.registerIcon(iconName);
        }

        public Icon icon() {
            return icon;
        }
    }

    private static class TurtlePeripheralItem extends SubItem {
        private final TurtlePeripheralType type;
        private final boolean visible;

        private TurtlePeripheralItem(TurtlePeripheralType type, int id, String name, String iconName, boolean visible) {
            super(id, name, iconName);
            this.type = type;
            this.visible = visible;
        }

        @Override
        public void addCreativeItems(int itemId, List<ItemStack> list) {
            if (visible) {
                list.add(new ItemStack(itemId, 1, id));
                Recipes.addUpgradedTurtles(list, type);
            }
        }
    }

    public static final SubItem WRITER_PCB = new TurtlePeripheralItem(TurtlePeripheralType.WRITER, 0, "item.pcb-writer", "cctags:pcb-writer", true);
    public static final SubItem PRINTER_PCB = new TurtlePeripheralItem(TurtlePeripheralType.PRINTER, 1, "item.pcb-printer", "cctags:pcb-printer", true);

    public static final SubItem HANDHELD_OLD = new SubItem(3, "item.tag-reader.old", "cctags:handheld") {
        @Override
        public void addCreativeItems(int itemId, List<ItemStack> list) {}
    };

    private final Map<Integer, SubItem> types;

    public ItemMisc(int id) {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CCTags.instance.tabTags);
        setUnlocalizedName("cctags-misc");

        ImmutableMap.Builder<Integer, SubItem> builder = ImmutableMap.builder();
        builder.put(WRITER_PCB.id, WRITER_PCB);
        builder.put(PRINTER_PCB.id, PRINTER_PCB);
        builder.put(HANDHELD_OLD.id, HANDHELD_OLD);
        types = builder.build();
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

    public ItemStack getStack(SubItem subtype) {
        return new ItemStack(this, 1, subtype.id);
    }

    public static boolean checkItem(ItemStack stack, SubItem subtype) {
        return stack != null &&
                stack.getItem() instanceof ItemMisc &&
                stack.getItemDamage() == subtype.id;
    }
}
