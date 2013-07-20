package boq.cctags.cc;

import static boq.utils.misc.Utils.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import boq.cctags.LuaInit;
import boq.cctags.tag.TagData;

import com.google.common.collect.ObjectArrays;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.turtle.api.ITurtleAccess;

public class TurtlePrinter extends TurtlePeripheral implements PrinterHelper.Printer {

    public TurtlePrinter(ITurtleAccess turtle) {
        super(turtle);
        helper = new PrinterHelper(this);
    }

    private int inkLevel;

    private final PrinterHelper helper;

    @Override
    public void setInkLevel(int inkLevel) {
        this.inkLevel = inkLevel;
    }

    @Override
    public int getInkLevel() {
        return inkLevel;
    }

    private final static String[] printerMethods = { "inkLevel", "loadInk", "print" };

    private final static String[] methods = ObjectArrays.concat(commonMethods, printerMethods, String.class);

    private final static int ownMethodStart = commonMethods.length;

    @Override
    public String getType() {
        return PeripheralType.PRINTER.peripheralType;
    }

    @Override
    public String[] getMethodNames() {
        return methods;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
        if (method < ownMethodStart)
            return super.callMethod(computer, context, method, arguments);

        if (method == ownMethodStart + 0) // inkLevel
            return wrap(inkLevel);

        if (method == ownMethodStart + 1) {// loadInk
            int slot = checkArg(arguments, 0) ? toInt(arguments[0]) : turtle.getSelectedSlot();

            ItemStack stack = turtle.getSlotContents(slot);
            if (stack == null)
                return FALSE;

            stack.stackSize--;

            if (stack.stackSize <= 0)
                turtle.setSlotContents(slot, null);

            return wrap(helper.addInk(stack));
        }

        if (method == ownMethodStart + 2) { // print
            if (!tagAccess.isValid())
                return wrap(false, "No tag");

            String icon = arguments[0].toString();
            String label = checkArg(arguments, 1) ? arguments[1].toString() : null;

            TagData data = tagAccess.readData();
            Object[] result = helper.printTag(data, icon, label);
            if (result[0].equals(true))
                tagAccess.writeData(data, true);

            return result;
        }

        throw new IllegalArgumentException("Invalid method id: " + method);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        inkLevel = tag.getInteger("InkLevel");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("InkLevel", inkLevel);
    }

    @Override
    public void attach(IComputerAccess computer) {
        super.attach(computer);

        LuaInit.mount(computer, "rom/programs/loadink", "loadink");
        LuaInit.mount(computer, "rom/help/icons", "icons");
        LuaInit.mount(computer, "rom/help/tag-printer", "turtle-printer-help");
    }

}
