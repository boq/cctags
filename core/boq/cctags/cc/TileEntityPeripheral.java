package boq.cctags.cc;

import static boq.utils.misc.Utils.wrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.tag.ItemTag;
import boq.cctags.tag.TagData;
import boq.utils.misc.Utils;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public abstract class TileEntityPeripheral<T extends WriterData> extends TileEntity implements IPeripheral {

    protected TagData readData() {
        if (data.tag == null)
            return null;

        return ItemTag.readData(data.tag);
    }

    protected boolean writeData(TagData tagData) {
        if (data.tag == null)
            return false;

        ItemTag.writeData(data.tag, tagData);
        return true;
    }

    protected final T data;

    public TileEntityPeripheral(T data) {
        this.data = data;
    }

    private ForgeDirection front() {
        int meta = getBlockMetadata();
        return BlockTagPeripheral.getFront(meta);
    }

    private PeripheralType type() {
        int meta = getBlockMetadata();
        return BlockTagPeripheral.getType(meta);
    }

    @Override
    public String getType() {
        return type().peripheralType;
    }

    protected final static String[] commonMethods = { "hasTag", "contents", "write", "size", "eject" };

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        switch (method) {
            case 0: // hasTag
                return wrap(data.tag != null);
            case 1: { // contents
                TagData data = readData();
                if (data == null)
                    return null;

                String contents = data.contents;
                return wrap(contents, contents.length());
            }
            case 2: { // write
                String newContents = arguments[0].toString();
                TagData data = readData();
                if (data == null)
                    return wrap(false, "No tag");

                if (!data.tagSize.check(newContents))
                    return wrap(false, "Message to big");

                data.contents = newContents;
                writeData(data);
                return wrap(true, data.contents.length());
            }
            case 3: { // size
                TagData data = readData();
                if (data == null)
                    return null;

                return wrap(data.tagSize.size, data.tagSize.name);
            }
            case 4: // eject
                return wrap(ejectTag());

            default:
                throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    @Override
    public boolean canAttachToSide(int side) {
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
        return dir != front();
    }

    @Override
    public void attach(IComputerAccess computer) {}

    @Override
    public void detach(IComputerAccess computer) {}

    public boolean ejectTag() {
        if (data.tag == null)
            return false;

        if (worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops"))
            Utils.dropItem(worldObj, xCoord, yCoord, zCoord, data.tag);

        data.tag = null;
        return true;
    }

    public boolean insertTag(ItemStack equipped) {
        if (data.tag != null && !ejectTag())
            return false;

        data.tag = equipped.copy();
        data.tag.stackSize = 1;
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        data.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        data.writeToNBT(tag);
    }

    public ItemStack getDroppedItem() {
        return data.tag;
    }
}
