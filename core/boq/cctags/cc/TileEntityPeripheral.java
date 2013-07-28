package boq.cctags.cc;

import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.cc.CommonCommands.IAccessHolder;
import boq.cctags.tag.access.*;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;
import boq.utils.log.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import dan200.computer.api.*;

public abstract class TileEntityPeripheral extends TileEntity implements IPeripheral {

    protected ITagAccess access = AccessUtils.NULL;

    protected Map<IComputerAccess, String> computers = Maps.newIdentityHashMap();

    protected ForgeDirection front() {
        int meta = getBlockMetadata();
        return BlockTagPeripheral.getFront(meta);
    }

    private ComputerPeripheralType type() {
        int meta = getBlockMetadata();
        return BlockTagPeripheral.getType(meta);
    }

    @Override
    public String getType() {
        return type().peripheralType;
    }

    protected final IAccessHolder holder = new IAccessHolder() {
        @Override
        public void setAccess(ITagAccess access) {
            TileEntityPeripheral.this.access = access;
        }

        @Override
        public ITagAccess getAccess() {
            return access;
        }
    };

    protected final IPositionProvider provider = new IPositionProvider() {
        @Override
        public Vec3 getPosition() {
            return Vec3.createVectorHelper(xCoord, yCoord, zCoord);
        }

        @Override
        public ForgeDirection getOrientation() {
            return front();
        }

        @Override
        public World getWorld() {
            return worldObj;
        }
    };

    private List<Command> commands;
    private String[] commandNames;

    protected void addCommands(ImmutableList.Builder<Command> commands) {
        commands.add(CommonCommands.createIsValid("hasTag", holder));
        commands.add(CommonCommands.createGetContents("contents", holder));
        commands.add(CommonCommands.createWriteContents("write", holder));
        commands.add(CommonCommands.createGetSize("size", holder));
        commands.add(CommonCommands.createGetSerial("serial", holder));
        commands.add(CommonCommands.createGetLibrary("library"));
        commands.add(CommonCommands.createGetAccessName("source", holder));
    }

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

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
        return Command.call(commands(), computer, context, method, arguments);
    }

    @Override
    public boolean canAttachToSide(int side) {
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
        return dir != front();
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.put(computer, computer.getAttachmentName());
        MountHelper.mount(computer, "rom/apis/tags", "tags-computer");
    }

    @Override
    public void detach(IComputerAccess computer) {
        String removed = computers.remove(computer);
        if (removed == null)
            Log.warning("Detached unknown computer %s from tag peripheral (%d,%d,%d)", computer, xCoord, yCoord, zCoord);
    }
}
