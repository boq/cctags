package boq.cctags.cc;

import static boq.utils.misc.Utils.checkArg;
import static boq.utils.misc.Utils.wrap;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import boq.cctags.cc.PrinterHelper.Printer;
import boq.cctags.tag.TagData;
import boq.cctags.tag.access.*;
import boq.cctags.tag.access.EntityAccess.IPositionProvider;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class CommonCommands {

    public interface IAccessHolder {
        public ITagAccess getAccess();

        public void setAccess(ITagAccess access);
    }

    public static Command createScanForTag(String name, final IAccessHolder owner, final IPositionProvider position) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                World w = position.getWorld();
                Preconditions.checkNotNull(w, "Unloaded world");

                String directionName = checkArg(arguments, 0) ? arguments[0].toString() : null;

                ForgeDirection front = position.getOrientation();
                final ForgeDirection dir = SidesHelper.localToWorld(front, directionName);

                ITagAccess access = AccessUtils.selectTag(position, dir);
                owner.setAccess(access);
                return wrap(access.isValid());
            }
        };
    }

    public static Command createIsValid(String name, final IAccessHolder holder) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                return wrap(holder.getAccess().isValid());
            }
        };
    }

    public static Command createGetContents(String name, final IAccessHolder holder) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                ITagAccess access = holder.getAccess();
                if (!access.isValid())
                    return wrap(false, "No tag");

                TagData data = access.readData();
                String contents = data.contents;
                return wrap(contents, contents == null ? 0 : contents.length());
            }
        };
    }

    public static Command createGetSize(String name, final IAccessHolder holder) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                ITagAccess access = holder.getAccess();
                if (!access.isValid())
                    return wrap(false, "No tag");

                TagData data = access.readData();

                return wrap(data.tagSize.size, data.tagSize.name);
            }
        };
    }

    public static Command createWriteContents(String name, final IAccessHolder holder) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                ITagAccess access = holder.getAccess();

                if (!access.isValid())
                    return wrap(false, "No tag");

                String newContents = arguments[0].toString();
                TagData data = access.readData();

                if (!data.tagSize.check(newContents))
                    return wrap(false, "Message to big");

                data.contents = newContents;
                access.writeData(data, false);
                return wrap(true, data.contents.length());
            }
        };
    }

    public static Command createGetSerial(String name, final IAccessHolder holder) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                ITagAccess access = holder.getAccess();

                if (!access.isValid())
                    return wrap(false, "No tag");

                return wrap(access.uid());
            }
        };
    }

    public static Command createGetLibrary(String name) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                return TagLibrary.instance.getLuaLibrary(arguments);
            }
        };
    }

    public static Command createGetAccessName(String name, final IAccessHolder holder) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                ITagAccess access = holder.getAccess();
                return wrap(access.isValid(), access.name());
            }
        };
    }

    public static Command createGetInkLevel(String name, final Printer printer) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                return wrap(printer.getInkLevel());
            }
        };
    }

    public static Command createPrint(String name, final IAccessHolder holder, final Printer printer) {
        return new Command(name) {
            @Override
            public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
                ITagAccess access = holder.getAccess();

                if (!access.isValid())
                    return wrap(false, "No tag");

                if (!access.isPrintable())
                    return wrap(false, "Can't print on this type of tag");

                TagData data = access.readData();

                if (arguments[0] == null)
                    return wrap(false, "Empty icon name");

                String icon = arguments[0].toString();

                String label = checkArg(arguments, 1) ? arguments[1].toString() : null;

                Object[] result = PrinterHelper.printTag(printer, data, icon, label);

                if (result[0].equals(true))
                    access.writeData(data, false);

                return result;
            }
        };
    }
}
