package boq.cctags.cc;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import boq.cctags.CCTags;
import boq.utils.log.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

import dan200.computer.api.IComputerAccess;

public class MountHelper {
    public final static MountHelper instance = new MountHelper();

    private static final String RELATIVE_PATH = "cctags" + File.separatorChar + "lua";
    
    private Map<String, File> luaFiles;
    
    private File luaFolder = getLuaFolder();
    
    private MountHelper() {}
    
    public void copyFiles() throws IOException {
        setupFiles();
    }
    
    private static Map<String, Long> readFileList() throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream listFile = closer.register(MountHelper.class.getResourceAsStream("/boq/cctags/lua/files.properties"));

            Map<String, Long> timestamps = Maps.newHashMap();

            Properties p = new Properties();
            p.load(listFile);

            for (String fileName : p.stringPropertyNames()) {
                String timestamp = p.getProperty(fileName);

                if (!Strings.isNullOrEmpty(timestamp))
                    try {
                        long t = Long.parseLong(timestamp);
                        timestamps.put(fileName, t);
                    } catch (NumberFormatException e) {
                        Log.warning("Ignoring invalid value in 'files.properties': " + timestamp);
                    }
                else
                    timestamps.put(fileName, null);
            }
            return timestamps;
        } finally {
            closer.close();
        }
    }

    private static File getLuaFolder() {
        File mcFolder = CCTags.proxy.getMcFolder();

        File luaFolder = new File(mcFolder, RELATIVE_PATH);

        Preconditions.checkState(!luaFolder.exists() || luaFolder.isDirectory(), "Path '%s' is not directort", luaFolder);
        if (!luaFolder.exists() && !luaFolder.mkdirs())
            throw new RuntimeException("Folder " + luaFolder + " cannot be created");

        return luaFolder;
    }
    
    private static void copyResourceToDist(String source, File destination) throws IOException
    {
        Closer closer = Closer.create();

        try {
            InputStream input = closer.register(MountHelper.class.getResourceAsStream(source));
            OutputStream output = closer.register(new FileOutputStream(destination));
            ByteStreams.copy(input, output);
        } finally {
            closer.close();
        }
    }
    
    public File getFilePath(String fileName) {
        File f = luaFiles.get(fileName);
        if (f == null) {
            f = new File(luaFolder, fileName);
            luaFiles.put(fileName, f);
        }
        
        return f;
    }
    
    private void setupFiles() throws IOException {
        Map<String, Long> files;
        files = readFileList();

        for (Entry<String, Long> entry : files.entrySet()) {
            String fileName = entry.getKey();
            Long timestamp = entry.getValue();

            File luaFile = getFilePath(fileName);

            if (luaFile.exists()) {
                if (!luaFile.canWrite()) {
                    Log.info("File %s is marked as read only, skipping", luaFile);
                    continue;
                }

                if (timestamp != null) {

                    long currentTimestamp = luaFile.lastModified();
                    if (currentTimestamp >= timestamp) {
                        if (currentTimestamp > timestamp)
                            Log.info("File '%s' is newer (%s) than resource (%s)", luaFile,
                                    new Date(currentTimestamp).toString(),
                                    new Date(timestamp).toString());

                        continue;
                    }
                }
            }

            String classpathName = "/boq/cctags/lua/" + fileName;
            Log.info("Creating or replacing file '%s' with '%s", luaFile, classpathName);

            copyResourceToDist(classpathName, luaFile);
            if (timestamp != null)
                luaFile.setLastModified(timestamp);
        }
    }

    public String getMount(String fileName) {
        return getFilePath(fileName).getAbsolutePath();
    }

    public static void mount(IComputerAccess computer, String path, String fileId) {
        String relPath = instance.getMount(fileId);
        String actualPath = computer.mountFixedDir(path, relPath, true, 0);
        if (!actualPath.equals(path))
            computer.unmount(actualPath);
    }
}
