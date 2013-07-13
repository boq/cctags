package boq.cctags;

import static boq.utils.misc.Utils.checkArg;
import static boq.utils.misc.Utils.wrap;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import argo.jdom.*;
import argo.saj.InvalidSyntaxException;
import boq.cctags.tag.TagSize;

import com.google.common.base.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IMount;

public class LuaInit {
    public final static LuaInit instance = new LuaInit();

    private LuaInit() {}

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
        public void list(String path, List<String> contents) throws IOException {

        }

        @Override
        public long getSize(String path) throws IOException {
            return 0;
        }

        @Override
        public InputStream openForRead(String path) throws IOException {
            return LuaInit.class.getResourceAsStream(file);
        }

    }

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

    private ImmutableMap<String, LibEntry> library = ImmutableMap.of();

    private static final JdomParser parser = new JdomParser();

    public IMount getMount(String fileName) {
        IMount result = mounts.get(fileName);
        Preconditions.checkNotNull(result, "Lua file %s cannot be found", fileName);
        return result;
    }

    public LibEntry getLibraryEntry(String name) {
        return library.get(name);
    }

    public ImmutableMap<String, LibEntry> getLibrary() {
        return library;
    }

    public void setup() {
        try {
            setupFiles();
            readLibrary();
        } catch (Throwable e) {
            Throwables.propagate(e);
        }
    }

    private static int getColor(JsonNode node, int defaultValue) {
        if (node.isNumberValue("color"))
            return Integer.parseInt(node.getNumberValue("color"));
        else if (node.isStringValue("color"))
            return Integer.parseInt(node.getStringValue("color"), 16);

        return defaultValue;
    }

    private void readLibrary() throws IOException, InvalidSyntaxException {
        Closer closer = Closer.create();
        try {
            InputStream listFile = closer.register(LuaInit.class.getResourceAsStream("/boq/cctags/lua/library.json"));
            Reader reader = new InputStreamReader(listFile);

            JsonRootNode root = parser.parse(reader);

            ImmutableMap.Builder<String, LibEntry> builder = ImmutableMap.builder();
            for (Entry<JsonStringNode, JsonNode> cat : root.getFields().entrySet()) {
                String category = cat.getKey().getText();
                JsonNode categoryData = cat.getValue();

                int categoryColor = getColor(categoryData, CCTags.config.DEFAULT_LIB_TAG_COLOR);
                String categoryIcon = categoryData.isNode("icon") ? categoryData.getNullableStringValue("icon") : null;

                JsonNode tags = categoryData.getNode("tags");
                for (Entry<JsonStringNode, JsonNode> entry : tags.getFields().entrySet()) {
                    String name = entry.getKey().getText();

                    JsonNode data = entry.getValue();
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

    private void setupFiles() throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream listFile = closer.register(LuaInit.class.getResourceAsStream("/boq/cctags/lua/files.txt"));

            for (String file : IOUtils.readLines(listFile, Charsets.UTF_8))
                mounts.put(file, new ResourceMount(filePrefix + file));

        } finally {
            closer.close();
        }
    }

    public static void mount(IComputerAccess computer, String path, String fileId) {
        IMount relPath = instance.getMount(fileId);
        String actualPath = computer.mount(path, relPath);
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
