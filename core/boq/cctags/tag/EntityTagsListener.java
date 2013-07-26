package boq.cctags.tag;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import boq.cctags.CCTags;
import boq.cctags.cc.ItemReader;
import boq.utils.misc.Utils;

public class EntityTagsListener {

    public static class TagProperty implements IExtendedEntityProperties {

        public TagData tagData;

        @Override
        public void saveNBTData(NBTTagCompound entityTag) {
            if (tagData != null) {
                NBTTagCompound tagTag = new NBTTagCompound();
                tagData.writeToNBT(tagTag, ItemTagUtils.embeddedSelector);
                entityTag.setTag(ItemTagUtils.EMBEDDED_TAG_PROPERTY, tagTag);
            }
        }

        @Override
        public void loadNBTData(NBTTagCompound entityTag) {
            if (entityTag.hasKey(ItemTagUtils.EMBEDDED_TAG_PROPERTY)) {
                tagData = new TagData();
                NBTTagCompound tag = entityTag.getCompoundTag(ItemTagUtils.EMBEDDED_TAG_PROPERTY);
                tagData.readFromNBT(tag, ItemTagUtils.embeddedSelector);
            }
        }

        @Override
        public void init(Entity entity, World world) {}
    }

    public static TagProperty getProperty(Entity entity) {
        IExtendedEntityProperties prop = entity.getExtendedProperties(ItemTagUtils.EMBEDDED_TAG_PROPERTY);
        if (prop instanceof TagProperty)
            return (TagProperty)prop;

        return null;
    }

    @ForgeSubscribe
    public void onEntityConstruct(EntityEvent.EntityConstructing evt) {
        if (evt.entity instanceof EntityLiving) // include player, let's see how it works...
            evt.entity.registerExtendedProperties(ItemTagUtils.EMBEDDED_TAG_PROPERTY, new TagProperty());
    }

    @ForgeSubscribe
    public void onEntityDeath(LivingDropsEvent evt) {
        IExtendedEntityProperties prop = evt.entity.getExtendedProperties(ItemTagUtils.EMBEDDED_TAG_PROPERTY);

        if (prop instanceof TagProperty) {
            TagProperty tag = (TagProperty)prop;
            if (tag.tagData != null) {
                ItemStack drop = CCTags.instance.itemTag.createFromData(tag.tagData);
                EntityItem entityItem = Utils.createDrop(evt.entity, drop);
                evt.drops.add(entityItem);
            }
        }
    }

    @ForgeSubscribe
    public void onEntityInteraction(EntityInteractEvent evt) {
        if (evt.target instanceof EntityLiving) {
            ItemStack stack = evt.entityPlayer.getHeldItem();

            if (stack != null && (stack.getItem() instanceof ItemReader)) {
                CCTags.instance.itemReader.interact(stack, evt.entityPlayer, (EntityLiving)evt.target);
                evt.setCanceled(true);
            }
        }

    }
}
