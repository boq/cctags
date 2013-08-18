package boq.cctags.cc;

import java.util.Map;

import boq.cctags.tag.access.ITagAccess;
import boq.utils.misc.Utils;

import com.google.common.collect.ImmutableList.Builder;

import dan200.computer.api.IComputerAccess;

public class TileEntityScanner extends TileEntityPeripheral {

    public TileEntityScanner() {}

    @Override
    protected void addCommands(Builder<Command> commands) {
        super.addCommands(commands);

        commands.add(CommonCommands.createScanForTag("scanForTag", holder, provider));
    }

    public void signalTagRead(ITagAccess access, int serial) {
        if (!access.isValid())
            return;

        this.access = access;
        for (Map.Entry<IComputerAccess, String> e : computers.entrySet())
            e.getKey().queueEvent("tag_scan", Utils.wrap(e.getValue(), serial));
    }
}
