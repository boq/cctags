package boq.cctags.cc;

import java.util.List;

import dan200.computer.api.IComputerAccess;

public abstract class Command {
    public final String name;

    public Command(String name) {
        this.name = name;
    }

    public abstract Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception;

    public static String[] extractNames(List<Command> commands) {
        String[] result = new String[commands.size()];

        int i = 0;
        for (Command c : commands)
            result[i++] = c.name;

        return result;
    }

    public static Object[] call(List<Command> commands, IComputerAccess computer, int method, Object[] arguments) throws Exception {
        try {
            Command c = commands.get(method);
            return c.callMethod(computer, method, arguments);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid method: " + method);
        }
    }
}
