package boq.cctags;

import static boq.utils.misc.Utils.checkArg;
import static boq.utils.misc.Utils.wrap;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import argo.jdom.*;
import argo.saj.InvalidSyntaxException;
import boq.cctags.tag.TagSize;
import boq.utils.log.Log;

import com.google.common.base.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

import dan200.computer.api.IComputerAccess;

public class LuaInit {
    public final static LuaInit instance = new LuaInit();

    private LuaInit() {}

    private Map<String, String> relPaths = Maps.newHashMap();

    public static class LibEntry {
        public final String icon;
        public final String label;
        public final String category;
        public final String comment;
        public final String contents;
        public final int color;
        public final TagSize size;

        private LibEntry(String icon, String label, int color, String category, String comment, String contents) {
            this.icon = icon;
            this.label = label;
            this.contents = contents;
            this.color = color & 0xFFFFFF;
            this.category = category;
            this.comment = comment;
            size = TagSize.fitSize(contents);
        }
    }

    private ImmutableMap<String, LibEntry> library;

    private static final JdomParser parser = new JdomParser();

    public String getRelPath(String fileName) {
        String result = relPaths.get(fileName);
        Preconditions.checkNotNull(result, "Lua file %s cannot be found", fileName);
        return result;
    }

    public LibEntry getLibraryEntry(String name) {
        return library.get(name);
    }

    public ImmutableMap<String, LibEntry> getLibrary() {
        return library;
    }

    private static final String RELATIVE_PATH = "cctags" + File.separatorChar + "lua";

    public void setup() {
        File luaFolder = getLuaFolder();

        try {
            setupFiles(luaFolder);
            readLibrary(luaFolder);
        } catch (Throwable e) {
            Throwables.propagate(e);
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

    private static int getColor(JsonNode node, int defaultValue) {
        if (node.isNumberValue("color"))
            return Integer.parseInt(node.getNumberValue("color"));
        else if (node.isStringValue("color"))
            return Integer.parseInt(node.getStringValue("color"), 16);

        return defaultValue;
    }

    private void readLibrary(File luaFolder) throws IOException, InvalidSyntaxException {
        Closer closer = Closer.create();
        try {
            InputStream listFile = closer.register(LuaInit.class.getResourceAsStream("/boq/cctags/lua/library.json"));
            Reader reader = new InputStreamReader(listFile);

            JsonRootNode root = parser.parse(reader);

            ImmutableMap.Builder<String, LibEntry> builder = ImmutableMap.builder();
            for (JsonNode categoryData : root.getElements()) {
                String category = categoryData.getStringValue("name");
                int categoryColor = getColor(categoryData, CCTags.config.DEFAULT_LIB_TAG_COLOR);
                String categoryIcon = categoryData.isNode("icon") ? categoryData.getNullableStringValue("icon") : null;

                JsonNode tags = categoryData.getNode("tags");
                for (JsonNode data : tags.getElements()) {
                    String name = data.getStringValue("name");
                    String icon = data.isNode("icon") ? data.getNullableStringValue("icon") : categoryIcon;
                    String label = data.isNode("label") ? data.getNullableStringValue("label") : null;
                    String comment = data.isNode("comment") ? data.getNullableStringValue("comment") : null;
                    String contents = data.getStringValue("contents");

                    int color = getColor(data, categoryColor);

                    builder.put(name, new LibEntry(icon, label, color, category, comment, contents));
                }
            }
            library = builder.build();
        } finally {
            closer.close();
        }
    }

    private void setupFiles(File luaFolder) throws IOException {
        Map<String, Long> files;
        files = readFileList();

        for (Entry<String, Long> entry : files.entrySet()) {
            String fileName = entry.getKey();
            Long timestamp = entry.getValue();

            File luaFile = new File(luaFolder, fileName);
            String relPath = RELATIVE_PATH + File.separatorChar + fileName;

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

                        relPaths.put(fileName, relPath);
                        continue;
                    }
                }
            }

            String classpathName = "/boq/cctags/lua/" + fileName;
            Log.info("Creating or replacing file '%s' with '%s", luaFile, classpathName);

            copyResourceToDist(classpathName, luaFile);
            if (timestamp != null)
                luaFile.setLastModified(timestamp);
            relPaths.put(fileName, relPath);
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

    public static void mount(IComputerAccess computer, String path, String fileId) {
        String relPath = instance.getRelPath(fileId);
        String actualPath = computer.mountFixedDir(path, relPath, true, 0);
        if (!actualPath.equals(path))
            computer.unmount(actualPath);
    }

    public Object[] getLuaLibrary(Object[] arguments) {
        Preconditions.checkArgument(checkArg(arguments, 0), "Missing parameter");

        String name = arguments[0].toString();
        LibEntry entry = library.get(name);
        if (entry == null)
            return wrap(null, "Invalid name");

        return wrap(entry.contents, entry.icon, entry.label);
    }
}
