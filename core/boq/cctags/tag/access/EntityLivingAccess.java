package boq.cctags.tag.access;

import net.minecraft.entity.EntityLiving;
import boq.cctags.tag.*;
import boq.cctags.tag.EntityTagsListener.TagProperty;

import com.google.common.base.Preconditions;

public class EntityLivingAccess extends EntityAccess<EntityLiving> {

    public EntityLivingAccess(EntityLiving entity, boq.cctags.tag.access.EntityAccess.IPositionProvider position) {
        super(entity, position);
    }

    @Override
    public String name() {
        return "living entity";
    }

    @Override
    protected boolean isValid(EntityLiving entity) {
        TagProperty props = EntityTagsListener.getProperty(entity);
        return props != null && props.tagData != null;
    }

    @Override
    protected TagData readData(EntityLiving entity) {
        TagProperty props = EntityTagsListener.getProperty(entity);
        Preconditions.checkNotNull(props, "Entity has not tag data");
        Preconditions.checkNotNull(props.tagData, "Entity has not tag data");
        return props.tagData;
    }

    @Override
    protected void writeData(EntityLiving entity, TagData data, boolean updateClients) {
        TagProperty props = EntityTagsListener.getProperty(entity);
        Preconditions.checkNotNull(props, "Entity has not tag data");
        Preconditions.checkNotNull(props.tagData, "Entity has not tag data");
        Preconditions.checkState(props.tagData == data, "Invalid usage");
    }

}
