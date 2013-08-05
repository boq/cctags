package boq.cctags.cc;

import static boq.utils.misc.Utils.*;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.cc.CommonCommands.IAccessHolder;
import boq.cctags.tag.access.*;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;
import boq.cctags.tag.access.ItemAccess.IStackProvider;

import com.google.common.collect.ImmutableList;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;

public abstract class TurtlePeripheral implements IHostedPeripheral {

    protected final TurtlePeripheralType type;

    protected final ITurtleAccess turtle;

    protected ITagAccess tagAccess = AccessUtils.NULL;

    public TurtlePeripheral(TurtlePeripheralType type, ITurtleAccess turtle) {
        this.type = type;
        this.turtle = turtle;
    }

    protected ForgeDirection getDirection(String direction) {
        int turtleDir = turtle.getFacingDir();
        ForgeDirection front = ForgeDirection.VALID_DIRECTIONS[turtleDir];
        return SidesHelper.localToWorld(front, direction);
    }

    @Override
    public String getType() {
        return type.peripheralType;
    }

    protected final IAccessHolder holder = new IAccessHolder() {

        @Override
        public void setAccess(ITagAccess access) {
            tagAccess = access;
        }

        @Override
        public ITagAccess getAccess() {
            return tagAccess;
        }
    };

    protected final IPositionProvider position = new IPositionProvider() {
        @Override
        public Vec3 getPosition() {
            return turtle.getPosition();
        }

        @Override
        public ForgeDirection getOrientation() {
            return ForgeDirection.VALID_DIRECTIONS[turtle.getFacingDir()];
        }

        @Override
        public World getWorld() {
            return turtle.getWorld();
        }
    };

    private List<Command> commands;
    private String[] commandNames;

    private List<Command> commands() {
        if (commands == null) {
            ImmutableList.Builder<Command> b = ImmutableList.builder();
            addCommands(b);
            commands = b.build();
        }

        return commands;
    }

    @Override
    public String[] getMethodNames() {
        if (commandNames == null)
            commandNames = Command.extractNames(commands());
        return commandNames;
    }

    protected void addCommands(ImmutableList.Builder<Command> commands) {
        commands.add(CommonCommands.createIsValid("isTagValid", holder));

        commands.add(new Command("selectFromSlot") {
            @Override
            public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
                final int slotId = checkArg(arguments, 0) ? toInt(arguments[0]) : turtle.getSelectedSlot();

                tagAccess = new ItemAccess(new IStackProvider() {
                    @Override
                    public ItemStack getStack() {
                        return turtle.getSlotContents(slotId);
                    }
                }, AccessUtils.mergedAccess);

                return wrap(tagAccess.isValid());
            }
        });

        commands.add(CommonCommands.createScanForTag("scanForTag", holder, position));
        commands.add(CommonCommands.createGetContents("contents", holder));
        commands.add(CommonCommands.createWriteContents("write", holder));
        commands.add(CommonCommands.createGetSize("size", holder));
        commands.add(CommonCommands.createGetSerial("serial", holder));
        commands.add(CommonCommands.createGetLibrary("library"));
        commands.add(CommonCommands.createGetAccessName("source", holder));
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        return Command.call(commands(), computer, method, arguments);
    }

    @Override
    public boolean canAttachToSide(int side) {
        return false;
    }

    @Override
    public void attach(IComputerAccess computer) {
        MountHelper.mount(computer, "rom/apis/tags", "tags-turtle");

        MountHelper.mount(computer, "rom/programs/follow", "follow");

        MountHelper.mount(computer, "rom/help/follow", "follow-help");
        MountHelper.mount(computer, "rom/help/tag-writer", "turtle-writer-help");
    }

    @Override
    public void detach(IComputerAccess computer) {}

    @Override
    public void update() {}

    @Override
    public void readFromNBT(NBTTagCompound tag) {}

    @Override
    public void writeToNBT(NBTTagCompound tag) {}
}
