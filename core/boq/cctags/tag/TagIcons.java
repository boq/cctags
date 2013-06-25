package boq.cctags.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import boq.cctags.client.RenderUtils;
import boq.utils.log.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

            @SideOnly(Side.CLIENT)
            @Override
            public void render(Tessellator tes, double xm, double ym, double xp, double yp, String argument) {
                Icon icon = instance.predefinedIcons.get(argument);
                RenderUtils.drawRectangle(tes, xm, ym, xp, yp, icon);
            }

        };
        // TODO: Text, bitmap

        public abstract boolean validate(String argument);

        @SideOnly(Side.CLIENT)
        public abstract void render(Tessellator tes, double xm, double ym, double xp, double yp, String argument);
    }

    public static class IconData {
        private final IconType type;
        private final String argument;

        private IconData(IconType type, String argument) {
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

    public static IconType getIconType(String typeId) {
        try {
            return IconType.valueOf(typeId.toUpperCase());
        } catch (IllegalArgumentException e) {
            Log.info("Invalid icon type: %s", typeId);
            return null;
        }
    }

    public static IconData parseIconString(String icon) {
        try {
            IconType type;
            int separatorIndex = icon.indexOf(':');
            if (separatorIndex == -1)
                type = IconType.PREDEFINED;
            else {
                String typeId = icon.substring(0, separatorIndex);

                type = getIconType(typeId);

                if (type == null)
                    return null;

                icon = icon.substring(separatorIndex + 1);
            }

            if (type.validate(icon))
                return new IconData(type, icon);
        } catch (Throwable t) {
            Log.warning(t, "Error during icon name '%s' validation", icon);
        }

        return null;
    }

    public boolean isValidIconString(String icon) {
        return parseIconString(icon) != null;
    }

    public List<String> listIcons(String iconType) {
        IconType type = getIconType(iconType);
        if (type != IconType.PREDEFINED)
            return ImmutableList.of();

        return ImmutableList.copyOf(predefinedIcons.keySet());
    }
}
