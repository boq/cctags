package boq.cctags.cc;

import com.google.common.collect.ImmutableList.Builder;

public class TileEntityScanner extends TileEntityPeripheral {

    public TileEntityScanner() {}

    @Override
    protected void addCommands(Builder<Command> commands) {
        super.addCommands(commands);

        commands.add(CommonCommands.createScanForTag("scanForTag", holder, provider));
    }
}
