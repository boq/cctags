package boq.cctags.cc;

import static boq.utils.misc.Utils.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.LuaInit;
import boq.cctags.tag.TagData;
import boq.cctags.tag.TagSize;
import boq.cctags.tag.access.*;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;
import boq.cctags.tag.access.ItemAccess.IStackProvider;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;

public abstract class TurtlePeripheral implements IHostedPeripheral {

    protected final ITurtleAccess turtle;

    protected ITagAccess tagAccess = AccessUtils.NULL;

    public TurtlePeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    protected ForgeDirection getDirection(String direction) {
        int turtleDir = turtle.getFacingDir();
        ForgeDirection front = ForgeDirection.VALID_DIRECTIONS[turtleDir];
        return SidesHelper.localToWorld(front, direction);
    }

    protected final static String[] commonMethods = { "isTagValid", "scanForTag", "selectFromSlot", "contents", "write", "size", "serial", "library", "source" };

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        switch (method) {
            case 0: // isTagValid
                return wrap(tagAccess.isValid());
            case 1: { // scanForTag
                String directionName = checkArg(arguments, 0) ? arguments[0].toString() : null;
                ForgeDirection dir = getDirection(directionName);
                ITagAccess access = AccessUtils.selectTag(turtle.getWorld(), dir, new IPositionProvider() {
                    @Override
                    public Vec3 getPosition() {
                        return turtle.getPosition();
                    }
                });

                if (!access.isValid())
                    return FALSE;

                tagAccess = access;
                return TRUE;
            }
            case 2: {// selectFromSlot
                final int slotId = checkArg(arguments, 0) ? toInt(arguments[0]) : turtle.getSelectedSlot();

                ITagAccess access = new ItemAccess(new IStackProvider() {
                    @Override
                    public ItemStack getStack() {
                        return turtle.getSlotContents(slotId);
                    }
                }, AccessUtils.mergedAccess);

                if (access.isValid()) {
                    tagAccess = access;
                    return TRUE;
                }

                return wrap(false, "Invalid or missing tag");

            }
            case 3: {// contents
                if (!tagAccess.isValid())
                    return wrap("false", "No selected tag");

                String contents = tagAccess.readData().contents;
                return wrap(contents, contents == null ? 0 : contents.length());
            }
            case 4: {// write
                if (!tagAccess.isValid())
                    return wrap("false", "No selected tag");

                String newContents = arguments[0].toString();

                TagData data = tagAccess.readData();
                if (!data.tagSize.check(newContents))
                    return wrap("false", "Message too big");

                data.contents = newContents;
                tagAccess.writeData(data, false);
                return wrap(true, newContents.length());
            }
            case 5: { // size
                if (!tagAccess.isValid())
                    return wrap("false", "No selected tag");

                TagSize size = tagAccess.readData().tagSize;
                return wrap(size.size, size.name);
            }

            case 6: { // serial
                if (!tagAccess.isValid())
                    return wrap("false", "No selected tag");

                int serial = tagAccess.uid();
                return wrap(serial);
            }

            case 7: // library
                return LuaInit.instance.getLuaLibrary(arguments);

            case 8: // source
                return wrap(tagAccess.name());
        }

        throw new IllegalArgumentException("Invalid method id: " + method);
    }

    @Override
    public boolean canAttachToSide(int side) {
        return false;
    }

    @Override
    public void attach(IComputerAccess computer) {
        LuaInit.mount(computer, "rom/apis/tags", "tags-turtle");

        LuaInit.mount(computer, "rom/programs/follow", "follow");

        LuaInit.mount(computer, "rom/help/follow", "follow-help");
        LuaInit.mount(computer, "rom/help/tag-writer", "turtle-writer-help");
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
