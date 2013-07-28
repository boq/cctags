package boq.cctags.cc;

import static boq.utils.misc.Utils.TRUE;
import static boq.utils.misc.Utils.wrap;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import boq.cctags.CCTags;
import boq.cctags.tag.TagData;
import boq.cctags.tag.TagIcons;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class PrinterHelper {
    private PrinterHelper() {}

    public interface Printer {
        public void setInkLevel(int inkLevel);

        public int getInkLevel();
    }

    private static boolean isBlackDye(ItemStack stack) {
        int oreId = OreDictionary.getOreID(stack);
        return oreId != -1 && OreDictionary.getOreName(oreId).equals("dyeBlack");
    }

    public static boolean tryPrint(Printer owner) {
        int level = owner.getInkLevel();
        if (level <= 0)
            return false;

        owner.setInkLevel(level - 1);
        return true;
    }

    public static boolean tryAdd(Printer owner, int amount) {
        int newAmount = owner.getInkLevel() + amount;
        if (newAmount > CCTags.config.MAX_PRINTER_CAPACITY)
            return false;

        owner.setInkLevel(newAmount);
        return true;
    }

    public static boolean addInk(Printer owner, ItemStack stack) {
        if (isBlackDye(stack))
            return tryAdd(owner, CCTags.config.PRINTER_USES_PER_INKSACK);

        return false;
    }

    public static Object[] printTag(Printer owner, TagData data, String icon, String label) {
        if (!TagIcons.instance.canIconBeCrafted(icon))
            return wrap(false, "Invalid icon name: " + icon);

        if (!tryPrint(owner))
            return wrap(false, "No ink");

        data.icon = icon;
        data.label = Strings.isNullOrEmpty(label) ? null : label;

        return TRUE;
    }

    public static String printIconList(String iconType) {
        List<String> icons = TagIcons.instance.listIcons(iconType);
        return Joiner.on(',').join(icons);
    }
}
