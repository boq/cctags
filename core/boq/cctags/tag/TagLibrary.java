package boq.cctags.tag;

import static boq.utils.misc.Utils.checkArg;
import static boq.utils.misc.Utils.wrap;

import java.io.*;

import argo.jdom.*;
import boq.cctags.CCTags;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closer;

public class TagLibrary {
    public final static TagLibrary instance = new TagLibrary();

    private TagLibrary() {}

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

    public LibEntry getLibraryEntry(String name) {
        return library.get(name);
    }

    public ImmutableMap<String, LibEntry> getLibrary() {
        return library;
    }

    private static int getColor(JsonNode node, int defaultValue) {
        if (node.isNumberValue("color"))
            return Integer.parseInt(node.getNumberValue("color"));
        else if (node.isStringValue("color"))
            return Integer.parseInt(node.getStringValue("color"), 16);

        return defaultValue;
    }

    public void readLibrary() throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream listFile = closer.register(TagLibrary.class.getResourceAsStream("/boq/cctags/lua/library.json"));
            Reader reader = new InputStreamReader(listFile);

            JsonRootNode root;
            try {
                root = parser.parse(reader);
            } catch (Throwable e) {
                throw Throwables.propagate(e);
            }

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

    public Object[] getLuaLibrary(Object[] arguments) {
        Preconditions.checkArgument(checkArg(arguments, 0), "Missing parameter");

        String name = arguments[0].toString();
        LibEntry entry = library.get(name);
        if (entry == null)
            return wrap(null, "Invalid name");

        return wrap(entry.contents, entry.icon, entry.label);
    }
}
