package boq.cctags.cc;

import dan200.turtle.api.ITurtleAccess;

public class TurtleWriter extends TurtlePeripheral {

    public TurtleWriter(ITurtleAccess turtle) {
        super(turtle);
    }

    private final static String[] methods = commonMethods;

    @Override
    public String getType() {
        return PeripheralType.WRITER.peripheralType;
    }

    @Override
    public String[] getMethodNames() {
        return methods;
    }
}
