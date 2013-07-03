package boq.cctags.cc;

import static boq.utils.misc.Utils.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.EntityPacketHandler;
import boq.cctags.LuaInit;
import boq.cctags.tag.*;
import boq.utils.coord.Bounds;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;

public abstract class TurtlePeripheral implements IHostedPeripheral {

    protected final ITurtleAccess turtle;

    protected TagAccess tagAccess;

    interface TagAccess {
        public boolean isValid();

        public TagData readData();

        public void writeData(TagData data, boolean updateClients);
    }

    private class EntityTagAccess implements TagAccess {

        private final EntityTag tag;

        private EntityTagAccess(EntityTag tag) {
            this.tag = tag;
        }

        @Override
        public boolean isValid() {
            if (tag.isDead)
                return false;

            Vec3 ownPos = turtle.getPosition();
            Vec3 tagPos = Vec3.createVectorHelper(tag.posX, tag.posY, tag.posZ);

            return ownPos.distanceTo(tagPos) < 2.0;
        }

        @Override
        public TagData readData() {
            return tag.data;
        }

        @Override
        public void writeData(TagData data, boolean updateClients) {
            Preconditions.checkState(data == tag.data);

            if (updateClients)
                EntityPacketHandler.sendUpdateToAllTrackers(tag);
        }
    }

    private class ItemTagAccess implements TagAccess {

        private final int slotId;

        private ItemTagAccess(int slotId) {
            this.slotId = slotId;
        }

        @Override
        public boolean isValid() {
            ItemStack stack = turtle.getSlotContents(slotId);
            return stack != null && stack.getItem() instanceof ItemTag && stack.stackSize == 1;
        }

        @Override
        public TagData readData() {
            ItemStack stack = turtle.getSlotContents(slotId);
            return ItemTag.readData(stack);
        }

        @Override
        public void writeData(TagData data, boolean updateClients) {
            ItemStack stack = turtle.getSlotContents(slotId);
            ItemTag.writeData(stack, data);
        }
    }

    public TurtlePeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    protected boolean isSelectedTagValid() {
        if (tagAccess == null)
            return false;

        if (!tagAccess.isValid()) {
            tagAccess = null;
            return false;
        }

        return true;
    }

    protected ForgeDirection getDirection(String direction) {
        int turtleDir = turtle.getFacingDir();
        ForgeDirection result = ForgeDirection.VALID_DIRECTIONS[turtleDir];

        if (direction == null || "front".equals(direction))
            return result;

        if ("up".equals(direction))
            return ForgeDirection.UP;

        if ("down".equals(direction))
            return ForgeDirection.DOWN;

        if ("back".equals(direction))
            return result.getOpposite();

        if ("left".equals(direction))
            return result.getRotation(ForgeDirection.DOWN);

        if ("right".equals(direction))
            return result.getRotation(ForgeDirection.UP);

        throw new IllegalArgumentException("Invalid direction, must be one of: up, down, front, back, left, right");
    }

    private final static double DELTA = 0.05;
    private final static Bounds searchBounds = new Bounds(-DELTA, -DELTA, -DELTA, 1 + DELTA, 1 + DELTA, 1 + DELTA);

    protected boolean selectTag(ForgeDirection direction) {
        Vec3 position = turtle.getPosition();
        AxisAlignedBB unitBox = searchBounds.getAabbFromPool(position.xCoord, position.yCoord, position.zCoord);
        World w = turtle.getWorld();

        ForgeDirection tagDirection = direction.getOpposite();
        for (Object o : w.getEntitiesWithinAABB(EntityTag.class, unitBox)) {
            EntityTag tag = (EntityTag)o;
            if (tag.data.side == tagDirection) {
                tagAccess = new EntityTagAccess(tag);
                return true;
            }
        }
        return false;
    }

    protected final static String[] commonMethods = { "isTagValid", "scanForTag", "selectFromSlot", "contents", "write", "size", "serial", "library" };

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        switch (method) {
            case 0: // isTagValid
                return wrap(isSelectedTagValid());
            case 1: { // scanForTag
                String directionName = checkArg(arguments, 0) ? arguments[0].toString() : null;
                return wrap(selectTag(getDirection(directionName)));
            }
            case 2: {// selectFromSlot
                int slotId = checkArg(arguments, 0) ? toInt(arguments[0]) : turtle.getSelectedSlot();
                TagAccess access = new ItemTagAccess(slotId);
                if (access.isValid()) {
                    tagAccess = access;
                    return TRUE;
                }

                return FALSE;

            }
            case 3: {// contents
                String contents = tagAccess.readData().contents;
                if (isSelectedTagValid())
                    return wrap(contents, contents == null ? 0 : contents.length());
                return null;
            }
            case 4: {// write
                String newContents = arguments[0].toString();
                if (isSelectedTagValid()) {
                    TagData data = tagAccess.readData();
                    if (!data.tagSize.check(newContents))
                        return wrap("false", "Message too big");

                    data.contents = newContents;
                    tagAccess.writeData(data, false);
                    return wrap(true, newContents.length());
                }
                return wrap(false, "No selected tag");
            }
            case 5: { // size
                if (!isSelectedTagValid())
                    return wrap("false", "No selected tag");

                TagSize size = tagAccess.readData().tagSize;
                return wrap(size.size, size.name);
            }

            case 6: { // serial
                if (!isSelectedTagValid())
                    return wrap("false", "No selected tag");

                int serial = tagAccess.readData().serial(turtle.getWorld());
                return wrap(serial);
            }

            case 7: // library
                return LuaInit.instance.getLuaLibrary(arguments);
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
