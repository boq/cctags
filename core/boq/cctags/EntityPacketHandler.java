package boq.cctags;

import java.io.*;
import java.lang.reflect.Field;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import boq.cctags.world.EntityTag;
import boq.cctags.world.TagData;
import boq.utils.serializable.ISelectableSerializableData.IFieldSelector;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.Player;

public class EntityPacketHandler implements PacketHandler.SubHandler {
    private static final IFieldSelector updateSelector = new IFieldSelector() {

        @Override
        public boolean canVisit(Field field, int flags) {
            return (flags & TagData.CLIENT_UPDATE) != 0;
        }
    };

    @Override
    public void handle(INetworkManager manager, Player player, DataInput input) throws IOException {
        int entityId = input.readInt();

        EntityPlayer pl = (EntityPlayer)player;
        Entity e = pl.worldObj.getEntityByID(entityId);

        if (e instanceof EntityTag) {
            final TagData data = ((EntityTag)e).data;
            data.readFromStream(input, updateSelector);
        }
    }

    private static void writeToUpdate(EntityTag tag, DataOutput output) throws IOException {
        output.writeInt(tag.entityId);
        tag.data.writeToStream(output, updateSelector);
    }

    private static Packet createUpdatePacket(EntityTag tag) {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = Constants.ENITIY_CHANNEL_ID;
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        try {
            writeToUpdate(tag, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        packet.data = output.toByteArray();
        return packet;
    }

    public static void sendUpdateToAllTrackers(EntityTag tag) {
        final World w = tag.worldObj;
        Preconditions.checkArgument(w instanceof WorldServer, "This method can only be runned on server side");

        WorldServer server = (WorldServer)w;

        Packet packet = createUpdatePacket(tag);
        server.getEntityTracker().sendPacketToAllPlayersTrackingEntity(tag, packet);
    }

    @Override
    public boolean checkSide(boolean isServer) {
        return !isServer;
    }
}
