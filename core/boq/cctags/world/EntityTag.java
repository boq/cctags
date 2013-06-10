package boq.cctags.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTag extends Entity {

    public EntityTag(World world) {
        super(world);
    }

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

    @Override
    public boolean func_85031_j(Entity entity)
    {
        if (entity instanceof EntityPlayer)
            return attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entity), 0);

        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, int amount)
    {
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

    private void dropItemStack() {
        entityDropItem(new ItemStack(Item.painting), 0.0F);
    }
}
