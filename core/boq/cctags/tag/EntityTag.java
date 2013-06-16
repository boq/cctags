package boq.cctags.tag;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.CCTags;
import boq.cctags.EntityPacketHandler;
import boq.utils.coord.Bounds;
import boq.utils.coord.BoundsRotator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTag extends Entity implements IEntityAdditionalSpawnData {

    public static double AABB_THICKNESS = 0.05;
    public static double AABB_MARGIN = 0.05;

    private final static Bounds canonicalBounds = new Bounds(-0.25 - AABB_MARGIN, -0.25 - AABB_MARGIN, 0.5 - AABB_THICKNESS,
            0.25 + AABB_MARGIN, 0.25 + AABB_MARGIN, 0.5 + AABB_THICKNESS);

    public EntityTag(World world) {
        super(world);
        data = new TagData();
        noClip = true;
        setSize(0.2f, 0.2f);
    }

    public EntityTag(World world, TagData data) {
        super(world);
        this.data = data;
        noClip = true;
        setSize(0.2f, 0.2f);
    }

    public final TagData data;

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    private void checkMovement(double x, double y, double z) {
        if (!worldObj.isRemote && !isDead && (x != 0 || y != 0 || z != 0)) {
            setDead();
            dropItemStack();
        }
    }

    @Override
    public void moveEntity(double dx, double dy, double dz) {
        checkMovement(dx, dy, dz);
    }

    @Override
    public void addVelocity(double vx, double vy, double vz) {
        checkMovement(vx, vy, vz);
    }

    private void updateAABB() {
        Bounds b = canonicalBounds.copy();
        BoundsRotator.flipDirection(b, data.side);
        b.setAABB(boundingBox, posX, posY, posZ);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;

        // possible in Entity constructor or before additional data is received
        if (data != null && data.side != null)
            updateAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
        // super method checks for collisions...
        super.setPositionAndRotation(par1, par3, par5, par7, par8);
    }

    @Override
    public boolean func_85031_j(Entity entity) {
        if (entity instanceof EntityPlayer)
            return attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entity), 0);

        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, int amount) {
        if (!isDead && !worldObj.isRemote)
        {
            setDead();
            setBeenAttacked();
            Entity entity = source.getEntity();
            boolean isCreative = entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode;

            if (!isCreative)
                dropItemStack();
        }

        return true;
    }

    @Override
    public void onUpdate() {}

    private ItemStack toItemStack() {
        int damage = data.tagSize.ordinal();
        ItemStack result = new ItemStack(CCTags.instance.itemTag, 1, damage);
        NBTTagCompound tag = ItemTag.getItemTag(result);

        data.writeToNBT(tag, ItemTag.nbtOnlySelector);
        return result;
    }

    @Override
    public ItemStack getPickedResult(MovingObjectPosition target) {
        // return toItemStack(); // called on client side, so we don't have all data...
        return null;
    }

    private void dropItemStack() {
        ItemStack stack = toItemStack();
        entityDropItem(stack, 0);
    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data) {
        this.data.writeToStream(data);
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data) {
        this.data.readFromStream(data);
        updateAABB();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound tagData = new NBTTagCompound();
        data.writeToNBT(tagData);
        tag.setTag("TagData", tagData);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound tagData = tag.getCompoundTag("TagData");
        data.readFromNBT(tagData);
        updateAABB();
    }

    @Override
    public float getBrightness(float par1) {
        return 1.0f;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        final ForgeDirection side = data.side;
        if (side != null) {
            int x = MathHelper.floor_double(posX + side.offsetX);
            int z = MathHelper.floor_double(posZ + side.offsetZ);

            if (worldObj.blockExists(x, 0, z)) {
                int y = MathHelper.floor_double(posY + side.offsetY);
                return worldObj.getLightBrightnessForSkyBlocks(x, y, z, 0);
            }
        }
        return 0;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!worldObj.isRemote) {
            data.rotation = data.rotation.rotateCCW();
            EntityPacketHandler.sendUpdateToAllTrackers(this);
        }
        return true;
    }

}
