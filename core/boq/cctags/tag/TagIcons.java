package boq.cctags.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import boq.cctags.CCTags;
import boq.cctags.client.RenderUtils;
import boq.utils.log.Log;

import com.google.common.base.Throwables;
import com.google.common.cache.*;
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
    public Icon iconBackgroundPaper;
    public Icon iconBackgroundGlass;

    private static boolean isSquare(int value, int max) {
        int square = 4;

        for (int i = 2; i <= max; i++) {
            if (square == value)
                return true;

            square += 2 * i + 1;
        }

        return false;
    }

    public enum IconType {
        NULL {
            @Override
            public boolean canBeCrafted(String argument) {
                return false;
            }

            @Override
            public boolean validate(String argument) {
                return false;
            }

            @Override
            @SideOnly(Side.CLIENT)
            public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {}
        },
        PREDEFINED {

            @Override
            public boolean validate(String argument) {
                return instance.predefinedIcons.containsKey(argument);
            }

            @SideOnly(Side.CLIENT)
            @Override
            public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
                tes.startDrawingQuads();
                tes.setNormal(0.0F, 0.0F, -1.0F);
                tes.setTranslation(0, 0, z);

                Icon icon = instance.predefinedIcons.get(argument);
                RenderUtils.drawRectangle(tes, xm, ym, xp, yp, icon);

                tes.draw();
            }

        },
        BITMAP {
            @Override
            public boolean canBeCrafted(String argument) {
                final int limit = CCTags.config.MAX_CRAFTABLE_BITMAP_TAG_SIZE;
                return argument.length() < limit * limit;
            }

            @Override
            public boolean validate(String argument) {
                if (!isSquare(argument.length(), CCTags.config.MAX_BITMAP_TAG_SIZE))
                    return false;

                for (char c : argument.toCharArray())
                    if (c != '1' && c != '0')
                        return false;

                return true;
            }

            @Override
            @SideOnly(Side.CLIENT)
            public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
                int len = argument.length();
                double sq = Math.sqrt(len);

                double dx = (xp - xm) / sq;
                double dy = (yp - ym) / sq;

                double x = xm;
                double y = ym;

                tes.startDrawingQuads();
                tes.setNormal(0.0F, 0.0F, -1.0F);
                tes.setTranslation(0, 0, z);
                tes.setColorOpaque(0, 0, 0);

                for (char c : argument.toCharArray()) {
                    if (c == '1')
                        RenderUtils.drawRectangle(tes, x, y, x + dx, y + dy);

                    x += dx;
                    if (x >= xp) {
                        y += dy;
                        x = xm;
                    }
                }

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tes.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

        };
        // TODO: Text

        public boolean canBeCrafted(String argument) {
            return true;
        }

        public abstract boolean validate(String argument);

        @SideOnly(Side.CLIENT)
        public abstract void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument);
    }

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

    public static IconType getIconType(String typeId) {
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
        return parseIconString(icon).canBeCrafted();
    }

    public List<String> listIcons(String iconType) {
        IconType type = getIconType(iconType);
        if (type != IconType.PREDEFINED)
            return ImmutableList.of();

        return ImmutableList.copyOf(predefinedIcons.keySet());
    }
}
