package boq.cctags.cc;

import static boq.utils.misc.Utils.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableList.Builder;

import dan200.computer.api.IComputerAccess;
import dan200.turtle.api.ITurtleAccess;

public class TurtleWriter extends TurtlePeripheral {

    public TurtleWriter(ITurtleAccess turtle) {
        super(TurtlePeripheralType.WRITER, turtle);
    }

    private final PrinterData data = new PrinterData();

    @Override
    protected void addCommands(Builder<Command> commands) {
        super.addCommands(commands);

        commands.add(CommonCommands.createGetInkLevel("inkLevel", data));
        commands.add(CommonCommands.createPrint("print", holder, data));

        commands.add(new Command("loadInk") {
            @Override
            public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
                int slot = checkArg(arguments, 0) ? toInt(arguments[0]) : turtle.getSelectedSlot();

                ItemStack stack = turtle.getSlotContents(slot);
                if (stack == null)
                    return FALSE;

                stack.stackSize--;

                if (stack.stackSize <= 0)
                    turtle.setSlotContents(slot, null);

                return wrap(PrinterHelper.addInk(data, stack));
            }
        });
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        data.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        data.writeToNBT(tag);
    }

    @Override
    public void attach(IComputerAccess computer) {
        super.attach(computer);

        MountHelper.mount(computer, "rom/programs/loadink", "loadink");
        MountHelper.mount(computer, "rom/help/icons", "icons");
    }

}
