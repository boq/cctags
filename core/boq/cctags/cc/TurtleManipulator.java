package boq.cctags.cc;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.CCTags;
import boq.cctags.Constants;
import boq.cctags.tag.*;
import boq.cctags.tag.EntityTagsListener.TagProperty;
import boq.utils.coord.Bounds;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.*;

public class TurtleManipulator implements ITurtleUpgrade {

    private final static double DELTA = 0.01;
    private final static Bounds manipulatorSearchBounds = new Bounds(-DELTA, -DELTA, -DELTA, 1 + DELTA, 1 + DELTA, 1 + DELTA);

    @Override
    public int getUpgradeID() {
        return Constants.TURTLE_MANIPULATOR;
    }

    @Override
    public String getAdjective() {
        return StatCollector.translateToLocal("item.tag-manipulator.name");
    }

    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.Tool;
    }

    @Override
    public ItemStack getCraftingItem() {
        return new ItemStack(CCTags.instance.itemManipulator);
    }

    @Override
    public boolean isSecret() {
        return false;
    }

    @Override
    public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return null;
    }

    @Override
    public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
        if (verb != TurtleVerb.Attack)
            return false;

        ItemStack toUse = turtle.getSlotContents(turtle.getSelectedSlot());

        if (toUse != null && !(toUse.getItem() instanceof ItemTag))
            return false;

        Vec3 pos = turtle.getPosition();
        ForgeDirection dir = ForgeDirection.getOrientation(direction);
        AxisAlignedBB aabb = manipulatorSearchBounds.createAABB(pos.xCoord + dir.offsetX, pos.yCoord + dir.offsetY, pos.zCoord + dir.offsetZ);

        for (Object o : turtle.getWorld().getEntitiesWithinAABB(EntityLiving.class, aabb)) {
            TagProperty props = EntityTagsListener.getProperty((EntityLiving)o);

            if (props != null) {
                if (props.tagData == null && toUse != null) {
                    props.tagData = ItemTagUtils.createFromStack(toUse);

                    if (--toUse.stackSize <= 0)
                        toUse = null;
                } else if (props.tagData != null && toUse == null) {
                    toUse = CCTags.instance.itemTag.createFromData(props.tagData);
                    props.tagData = null;
                } else
                    continue;

                turtle.setSlotContents(turtle.getSelectedSlot(), toUse);
                return true;
            }
        }
        return false;
    }

    @Override
    public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
        return CCTags.instance.itemManipulator.getIconFromDamage(0);
    }

}
