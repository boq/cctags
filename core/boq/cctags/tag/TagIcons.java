package boq.cctags.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import boq.utils.log.Log;

import com.google.common.base.Throwables;
import com.google.common.cache.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

public class TagIcons {

    private TagIcons() {}

    public static final TagIcons instance = new TagIcons();

    Map<String, Icon> predefinedIcons = Maps.newHashMap();

    public Icon iconMarker;
    public Icon iconBackgroundPaper;
    public Icon iconBackgroundGlass;

    public final static IconData NULL_ICON = new IconData(IconType.NULL, "");

    public static class IconData {
        public final IconType type;
        private final String argument;

        private IconData(IconType type, String argument) {
            this.type = type;
            this.argument = argument;
        }

        public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z) {
            type.render(tes, xm, ym, xp, yp, z, argument);
        }

        @Override
        public String toString() {
            return "icon:" + type + ":" + argument;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * (prime + argument.hashCode()) + type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj instanceof IconData) {
                IconData other = (IconData)obj;
                return type == other.type &&
                        argument.equals(other.argument);
            }

            return false;
        }

        public boolean canBeCrafted() {
            return type.canBeCrafted(argument);
        }

        public String getDescription() {
            return type.getDescription(argument);
        }
    }

    public void loadPredefinedIcons() {
        try {
            Closer closer = Closer.create();
            try {
                InputStream is = closer.register(TagIcons.class.getResourceAsStream("/assets/cctags/textures/items/icons.txt"));

                for (String s : IOUtils.readLines(is, Charsets.UTF_8))
                    addIconName(s);
            } finally {
                closer.close();
            }

        } catch (IOException e) {
            Log.warning(e, "Can't load file 'icons.properties'. No predefined icons");
            return;
        }
    }

    public void addIconName(String name) {
        predefinedIcons.put(name, null);
    }

    public void registerIcons(IconRegister registry) {
        if (iconMarker == null)
            iconMarker = registry.registerIcon("cctags:icon-marker");

        if (iconBackgroundPaper == null)
            iconBackgroundPaper = registry.registerIcon("cctags:icon-background-paper");

        if (iconBackgroundGlass == null)
            iconBackgroundGlass = registry.registerIcon("cctags:icon-background-glass");

        for (Map.Entry<String, Icon> entry : predefinedIcons.entrySet())
            if (entry.getValue() == null) {
                String name = entry.getKey();
                Icon icon = registry.registerIcon("cctags:icon-" + name);
                entry.setValue(icon);
            }
    }

    private static IconType getIconType(String typeId) {
        try {
            return IconType.valueOf(typeId.toUpperCase());
        } catch (IllegalArgumentException e) {
            Log.info("Invalid icon type: %s", typeId);
            return null;
        }
    }

    private LoadingCache<String, IconData> iconDataCache =
            CacheBuilder.newBuilder().
                    maximumSize(32).
                    build(new CacheLoader<String, IconData>() {

                        @Override
                        public IconData load(String key) throws Exception {
                            return parseIconString(key);
                        }

                    });

    private static IconData parseIconString(String icon) {
        try {
            if (icon == null)
                return NULL_ICON;

            IconType type;
            int separatorIndex = icon.indexOf('!');
            if (separatorIndex == -1)
                type = IconType.PREDEFINED;
            else {
                String typeId = icon.substring(0, separatorIndex);

                type = getIconType(typeId);

                if (type == null)
                    return NULL_ICON;

                icon = icon.substring(separatorIndex + 1);
            }

            if (type.validate(icon))
                return new IconData(type, icon);
        } catch (Throwable t) {
            Log.warning(t, "Error during icon name '%s' validation", icon);
        }

        return NULL_ICON;
    }

    public IconData getIconData(String description) {
        try {
            return iconDataCache.get(description);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    public boolean canIconBeCrafted(String icon) {
        return getIconData(icon).canBeCrafted();
    }

    public List<String> listIcons(String iconType) {
        IconType type = getIconType(iconType);
        if (type != IconType.PREDEFINED)
            return ImmutableList.of();

        return ImmutableList.copyOf(predefinedIcons.keySet());
    }
}
