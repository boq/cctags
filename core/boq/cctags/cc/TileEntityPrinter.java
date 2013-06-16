package boq.cctags.cc;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import boq.cctags.CCTags;
import boq.cctags.client.TagIcons;
import boq.cctags.tag.TagData;

import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;

import dan200.computer.api.IComputerAccess;

public class TileEntityPrinter extends TileEntityPeripheral<PrinterData> {

    public TileEntityPrinter() {
        super(new PrinterData());
    }

    private final static String[] printerMethods = { "inkLevel", "print" };

    private final static String[] methods = ObjectArrays.concat(commonMethods, printerMethods, String.class);

    private final static int ownMethodStart = commonMethods.length;

    @Override
    public String[] getMethodNames() {
        return methods;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        if (method < ownMethodStart)
            return super.callMethod(computer, method, arguments);

        if (method == ownMethodStart + 0) // intLevel
            return wrap(data.inkLevel);
        else if (method == ownMethodStart + 1) {// print
            String type = arguments[0].toString();
            String descr = arguments[1].toString();
            String label = null;

            if (arguments.length > 2)
                label = arguments[2].toString();

            if ("predefined".equals(type)) {
                if (!TagIcons.instance.isValidIconName(descr))
                    return wrap(false, "Unknown predefined icon: " + descr);

                TagData data = readData();

                if (data == null)
                    return wrap(false, "No tag");

                if (!tryPrint())
                    return wrap(false, "No ink");

                data.icon = descr;
                data.label = Strings.isNullOrEmpty(label) ? null : label;

                writeData(data);
            } else
                return wrap(false, "Unknown icon type: " + type);

            return TRUE;
        }

        throw new IllegalArgumentException("Unknown method: " + method);
    }

    private static boolean isBlackDye(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemDye && stack.getItemDamage() == 0;
    }

    private boolean tryPrint() {
        if (data.inkLevel <= 0)
            return false;

        data.inkLevel--;
        return true;
    }

    private boolean tryAdd(int amount) {
        int newAmount = data.inkLevel + amount;
        if (newAmount > CCTags.config.MAX_PRINTER_CAPACITY)
            return false;

        data.inkLevel = newAmount;
        return true;
    }

    public boolean addInk(ItemStack stack) {
        if (isBlackDye(stack))
            return tryAdd(CCTags.config.PRINTER_USES_PER_INKSACK);

        return false;
    }
}
