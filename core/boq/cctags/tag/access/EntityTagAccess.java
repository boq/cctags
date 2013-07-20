package boq.cctags.tag.access;

import boq.cctags.EntityPacketHandler;
import boq.cctags.tag.EntityTag;
import boq.cctags.tag.TagData;

import com.google.common.base.Preconditions;

public class EntityTagAccess extends EntityAccess<EntityTag> {

    public EntityTagAccess(EntityTag tag, IPositionProvider position) {
        super(tag, position);
    }

    @Override
    protected TagData readData(EntityTag e) {
        return e.data;
    }

    @Override
    protected void writeData(EntityTag e, TagData data, boolean updateClients) {
        Preconditions.checkState(data == e.data);

        if (updateClients)
            EntityPacketHandler.sendUpdateToAllTrackers(e);
    }

    @Override
    protected boolean isValid(EntityTag entity) {
        return true;
    }

    @Override
    public String name() {
        return "tag";
    }
}
