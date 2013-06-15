package boq.cctags;

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import boq.utils.log.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public interface SubHandler {
        public void handle(INetworkManager manager, Player player, DataInput input) throws IOException;

        public boolean checkSide(boolean isServer);
    }

    private final Map<String, SubHandler> handlers = Maps.newHashMap();

    public PacketHandler() {
        handlers.put(Constants.ENITIY_CHANNEL_ID, new EntityPacketHandler());
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        final String channel = packet.channel;
        final boolean isServer = CCTags.proxy.isServer();
        try {
            SubHandler h = handlers.get(channel);
            Preconditions.checkNotNull(h, "Invalid channel: %s", channel);
            if (h.checkSide(isServer)) {
                DataInput input = ByteStreams.newDataInput(packet.data);
                h.handle(manager, player, input);
            } else
                Log.warning("Message on channel %s from player %s sent into wrong direction", channel, player);
        } catch (Exception e) {
            if (isServer)
                Log.severe(e, "Invalid message from player %s on channel %s", player, channel);
            else
                Log.severe(e, "Invalid message from server on channel %s", channel);
        }
    }

}
