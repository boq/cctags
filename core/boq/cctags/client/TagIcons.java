package boq.cctags.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import boq.utils.log.Log;

import com.google.common.collect.Maps;
import com.google.common.io.Closer;

public class TagIcons {

    private TagIcons() {}

    public static final TagIcons instance = new TagIcons();

    private Map<String, Icon> predefinedIcons = Maps.newHashMap();

    public Icon iconMarker;

    public Icon iconBackground;

    public enum IconType {
        PREDEFINED {

            @Override
            public boolean validate(String argument) {
                return instance.predefinedIcons.containsKey(argument);
            }

            @Override
            public void render(Tessellator tes, double xm, double ym, double xp, double yp, String argument) {
                Icon icon = instance.predefinedIcons.get(argument);
                RenderUtils.drawRectangle(tes, xm, ym, xp, yp, icon);
            }

        };
        // TEXT

        public abstract boolean validate(String argument);

        public abstract void render(Tessellator tes, double xm, double ym, double xp, double yp, String argument);
    }

    public static class IconRender {
        private final IconType type;
        private final String argument;

        private IconRender(IconType type, String argument) {
            this.type = type;
            this.argument = argument;
        }

        public void render(Tessellator tes, double xm, double ym, double xp, double yp) {
            type.render(tes, xm, ym, xp, yp, argument);

        }
    }

    public void loadPredefinedIcons() {
        try {
            Closer closer = Closer.create();
            try {
                InputStream is = closer.register(TagIcons.class.getResourceAsStream("/mods/cctags/textures/items/icons.properties"));
                Properties p = new Properties();
                p.load(is);

                for (String s : p.stringPropertyNames())
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

        if (iconBackground == null)
            iconBackground = registry.registerIcon("cctags:icon-background");

        for (Map.Entry<String, Icon> entry : predefinedIcons.entrySet())
            if (entry.getValue() == null) {
                String name = entry.getKey();
                Icon icon = registry.registerIcon("cctags:icon-" + name);
                entry.setValue(icon);
            }
    }

    public static IconRender parseIconString(String icon) {
        IconType type;
        int separatorIndex = icon.indexOf(':');
        if (separatorIndex == -1)
            type = IconType.PREDEFINED;
        else {
            String typeId = icon.substring(0, separatorIndex).toUpperCase();

            try {
                type = IconType.valueOf(typeId);
            } catch (IllegalArgumentException e) {
                return null;
            }

            icon = icon.substring(separatorIndex + 1);
        }

        if (type.validate(icon))
            return new IconRender(type, icon);

        return null;
    }

    public boolean isValidIconString(String icon) {
        return parseIconString(icon) != null;
    }
}
