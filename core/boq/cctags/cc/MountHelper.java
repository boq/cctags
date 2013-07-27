package boq.cctags.cc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IMount;

public class MountHelper {
    public final static MountHelper instance = new MountHelper();

    private MountHelper() {}

    private Map<String, IMount> mounts = Maps.newHashMap();

    private static final String filePrefix = "/boq/cctags/lua/";

    private static class ResourceMount implements IMount {

        private final String file;

        public ResourceMount(String file) {
            this.file = file;
        }

        @Override
        public boolean exists(String path) throws IOException {
            return Strings.isNullOrEmpty(path);
        }

        @Override
        public boolean isDirectory(String path) throws IOException {
            return false;
        }

        @Override
        public void list(String path, List<String> contents) throws IOException {}

        @Override
        public long getSize(String path) throws IOException {
            return 0;
        }

        @Override
        public InputStream openForRead(String path) throws IOException {
            return MountHelper.class.getResourceAsStream(file);
        }

    }

    public IMount getMount(String fileName) {
        IMount result = mounts.get(fileName);

        if (result == null) {
            result = new ResourceMount(filePrefix + fileName);
            mounts.put(fileName, result);
        }
        Preconditions.checkNotNull(result, "Lua file %s cannot be found", fileName);
        return result;
    }

    public static void mount(IComputerAccess computer, String path, String fileId) {
        IMount relPath = instance.getMount(fileId);
        String actualPath = computer.mount(path, relPath);
        if (!actualPath.equals(path))
            computer.unmount(actualPath);
    }
}
