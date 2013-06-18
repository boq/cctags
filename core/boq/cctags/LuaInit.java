package boq.cctags;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import boq.utils.log.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

public class LuaInit {
    public final static LuaInit instance = new LuaInit();

    private LuaInit() {}

    public void setupFiles() {
        Map<String, Long> files;
        try {
            files = readFileList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File mcFolder = CCTags.proxy.getMcFolder();
        File luaFolder = new File(mcFolder, "cctags" + File.separatorChar + "lua");

        Preconditions.checkState(!luaFolder.exists() || luaFolder.isDirectory(), "Path '%s' is not directort", luaFolder);
        if (!luaFolder.exists() && !luaFolder.mkdirs())
            throw new RuntimeException("Folder " + luaFolder + " cannot be created");

        for (Entry<String, Long> entry : files.entrySet()) {
            String fileName = entry.getKey();
            Long timestamp = entry.getValue();

            File luaFile = new File(luaFolder, fileName);

            if (timestamp != null && luaFile.exists()) {
                long currentTimestamp = luaFile.lastModified();
                if (currentTimestamp >= timestamp) {
                    if (currentTimestamp > timestamp)
                        Log.info("File '%s' is newer (%s) than resource (%s)", luaFile,
                                new Date(currentTimestamp).toString(),
                                new Date(timestamp).toString());
                    break;
                }
            }

            String classpathName = "/boq/cctags/lua/" + fileName;
            Log.info("Creating or replacing file '%s' with '%s", luaFile, classpathName);

            try {
                copyResourceToDist(classpathName, luaFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy file", e);
            }
        }
    }

    private static void copyResourceToDist(String source, File destination) throws IOException
    {
        Closer closer = Closer.create();

        try {
            InputStream input = closer.register(LuaInit.class.getResourceAsStream(source));
            OutputStream output = closer.register(new FileOutputStream(destination));
            ByteStreams.copy(input, output);
        } finally {
            closer.close();
        }
    }

    private static Map<String, Long> readFileList() throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream listFile = closer.register(LuaInit.class.getResourceAsStream("/boq/cctags/lua/files.properties"));

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
}
