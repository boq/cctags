package boq.cctags.cc;

import static boq.utils.misc.Utils.wrap;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.LuaInit;
import boq.cctags.tag.ItemTag;
import boq.cctags.tag.TagData;
import boq.utils.log.Log;
import boq.utils.misc.Utils;

import com.google.common.collect.Maps;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public abstract class TileEntityPeripheral<T extends WriterData> extends TileEntity implements IPeripheral, IInventory {

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

    protected Map<IComputerAccess, String> computers = Maps.newIdentityHashMap();

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
                return wrap(contents, contents == null ? 0 : contents.length());
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
                return wrap(ejectTag(true));

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
    public void attach(IComputerAccess computer) {
        final LuaInit reg = LuaInit.instance;
        computers.put(computer, computer.getAttachmentName());
        computer.mountFixedDir("rom/apis/tags", reg.getRelPath("tags-computer"), true, 0);

        computer.mountFixedDir("rom/programs/clonetag", reg.getRelPath("clonetag"), true, 0);
        computer.mountFixedDir("rom/programs/writetag", reg.getRelPath("writetag"), true, 0);
        computer.mountFixedDir("rom/programs/readtag", reg.getRelPath("readtag"), true, 0);
        computer.mountFixedDir("rom/programs/progtag", reg.getRelPath("program"), true, 0);
        computer.mountFixedDir("rom/help/progtag", reg.getRelPath("program-help"), true, 0);
    }

    @Override
    public void detach(IComputerAccess computer) {
        String removed = computers.remove(computer);
        if (removed == null)
            Log.warning("Detached unknown computer %s from tag peripheral (%d,%d,%d)", computer, xCoord, yCoord, zCoord);
    }

    public boolean ejectTag(boolean update) {
        if (data.tag == null)
            return false;

        if (worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops"))
            Utils.dropItem(worldObj, xCoord, yCoord, zCoord, data.tag);

        data.tag = null;

        if (update) {
            int meta = BlockTagPeripheral.clearActive(getBlockMetadata());
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
            onInventoryChanged();
        }
        return true;
    }

    protected void onTagInsert() {
        for (Map.Entry<IComputerAccess, String> access : computers.entrySet())
            access.getKey().queueEvent("tag", wrap(access.getValue()));

        int meta = BlockTagPeripheral.setActive(getBlockMetadata());
        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
        onInventoryChanged();
    }

    public boolean insertTag(ItemStack equipped) {
        if (data.tag != null && !ejectTag(false))
            return false;

        data.tag = equipped.copy();
        data.tag.stackSize = 1;
        onTagInsert();
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

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return (slot == 0) ? data.tag : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot != 0)
            return null;

        ItemStack stack = data.tag;
        data.tag = null;
        onInventoryChanged();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 0) {
            data.tag = stack;
            onTagInsert();
        }
    }

    @Override
    public String getInvName() {
        return "peripheral-tag";
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openChest() {}

    @Override
    public void closeChest() {}

    @Override
    public boolean isStackValidForSlot(int i, ItemStack stack) {
        return (i == 0) &&
                (data.tag == null) && // only one item allowed
                (stack != null) &&
                (stack.getItem() instanceof ItemTag);
    }
}
