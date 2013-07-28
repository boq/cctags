package boq.cctags.cc;

import static boq.utils.misc.Utils.checkArg;
import static boq.utils.misc.Utils.wrap;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.tag.TagData;
import boq.cctags.tag.TagLibrary;
import boq.cctags.tag.access.*;
import boq.cctags.tag.access.ItemAccess.IStackProvider;
import boq.utils.log.Log;
import boq.utils.misc.InventoryUtils;
import boq.utils.misc.Utils;

import com.google.common.collect.Maps;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public abstract class TileEntityPeripheral<T extends WriterData> extends TileEntity implements IPeripheral, IInventory {

    protected ITagAccess access;

    protected final T data;

    protected Map<IComputerAccess, String> computers = Maps.newIdentityHashMap();

    public TileEntityPeripheral(final T data) {
        this.data = data;
        access = new ItemAccess(new IStackProvider() {
            @Override
            public ItemStack getStack() {
                return data.insertedItem;
            }
        }, AccessUtils.mergedAccess);
    }

    private ForgeDirection front() {
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

    protected final static String[] commonMethods = { "hasTag", "contents", "write", "size", "eject", "serial", "library", "source" };

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        switch (method) {
            case 0: // hasTag
                return wrap(access.isValid());
            case 1: { // contents
                if (!access.isValid())
                    return wrap(false, "No tag");

                TagData data = access.readData();
                String contents = data.contents;
                return wrap(contents, contents == null ? 0 : contents.length());
            }
            case 2: { // write
                if (!access.isValid())
                    return wrap(false, "No tag");

                String newContents = arguments[0].toString();
                TagData data = access.readData();

                if (!data.tagSize.check(newContents))
                    return wrap(false, "Message to big");

                data.contents = newContents;
                access.writeData(data, false);
                return wrap(true, data.contents.length());
            }
            case 3: { // size
                if (!access.isValid())
                    return wrap(false, "No tag");

                TagData data = access.readData();

                return wrap(data.tagSize.size, data.tagSize.name);
            }
            case 4: // eject
                String direction = checkArg(arguments, 0) ? arguments[0].toString() : null;
                if (direction != null) {
                    ForgeDirection dir = SidesHelper.localToWorld(front().getOpposite(), direction);
                    return wrap(ejectTag(dir, true));
                }
                return wrap(ejectTag(true));

            case 5: {// serial
                if (!access.isValid())
                    return wrap(false, "No tag");

                return wrap(access.uid());
            }
            case 6: // library
                return TagLibrary.instance.getLuaLibrary(arguments);
            case 7: // source
                return wrap(access.name());
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
        computers.put(computer, computer.getAttachmentName());
        MountHelper.mount(computer, "rom/apis/tags", "tags-computer");

        MountHelper.mount(computer, "rom/programs/clonetag", "clonetag");
        MountHelper.mount(computer, "rom/programs/writetag", "writetag");
        MountHelper.mount(computer, "rom/programs/readtag", "readtag");
        MountHelper.mount(computer, "rom/programs/progtag", "program");

        MountHelper.mount(computer, "rom/help/progtag", "program-help");
        MountHelper.mount(computer, "rom/help/clonetag", "clonetag-help");
        MountHelper.mount(computer, "rom/help/tag-writer", "computer-writer-help");
    }

    @Override
    public void detach(IComputerAccess computer) {
        String removed = computers.remove(computer);
        if (removed == null)
            Log.warning("Detached unknown computer %s from tag peripheral (%d,%d,%d)", computer, xCoord, yCoord, zCoord);
    }

    public boolean giveTagToPlayer(EntityPlayer player, boolean update) {
        ItemStack ejected = removeTag(update);

        if (ejected == null)
            return false;

        player.setCurrentItemOrArmor(0, ejected);
        return true;
    }

    public boolean ejectTag(ForgeDirection side, boolean update) {
        ItemStack ejected = removeTag(update);

        if (ejected == null)
            return false;

        TileEntity neighbor = worldObj.getBlockTileEntity(xCoord + side.offsetX,
                yCoord + side.offsetY, zCoord + side.offsetZ);

        if (neighbor instanceof IInventory)
            ejected = InventoryUtils.insertStack((IInventory)neighbor, ejected, side.getOpposite());

        if (ejected == null)
            return true;

        if (worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops"))
            Utils.dropItem(worldObj, xCoord, yCoord, zCoord, ejected);

        return false;
    }

    public boolean ejectTag(boolean update) {
        ItemStack ejected = removeTag(update);

        if (ejected == null)
            return false;

        if (worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops"))
            Utils.dropItem(worldObj, xCoord, yCoord, zCoord, ejected);

        return true;
    }

    public ItemStack removeTag(boolean update) {
        if (data.insertedItem == null)
            return null;

        ItemStack tmp = data.insertedItem;
        data.insertedItem = null;

        if (update) {
            int meta = BlockTagPeripheral.clearActive(getBlockMetadata());
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
            onInventoryChanged();
        }
        return tmp;
    }

    protected void onTagInsert() {
        if (access.isValid()) {
            for (Map.Entry<IComputerAccess, String> access : computers.entrySet())
                access.getKey().queueEvent("tag", wrap(access.getValue()));

            int meta = BlockTagPeripheral.setActive(getBlockMetadata());
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
        }
        onInventoryChanged();
    }

    public boolean insertTag(ItemStack equipped) {
        if (data.insertedItem != null && !ejectTag(false))
            return false;

        data.insertedItem = equipped.copy();
        data.insertedItem.stackSize = 1;
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
        return data.insertedItem;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return (slot == 0) ? data.insertedItem : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot != 0)
            return null;

        ItemStack stack = data.insertedItem;
        data.insertedItem = null;
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
            data.insertedItem = stack;
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
                (data.insertedItem == null) && // only one item allowed
                (stack != null);
    }
}
