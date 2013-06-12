package boq.cctags.client;

import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import boq.cctags.Constants;

import com.google.common.collect.Maps;

public class TagIcons {

    private TagIcons() {}

    public static final TagIcons instance = new TagIcons();

    private Map<String, Icon> tagIcons = Maps.newHashMap();

    public Icon iconMarker;

    public Icon iconBackground;

    public void addIconNames() {
        addIconName("left");
        addIconName("right");
        addIconName("forward");
        addIconName(Constants.DEFAULT_ICON);
    }

    public void addIconName(String name) {
        tagIcons.put(name, null);
    }

    public void registerIcons(IconRegister registry) {
        if (iconMarker == null)
            iconMarker = registry.registerIcon("cctags:icon-marker");

        if (iconBackground == null)
            iconBackground = registry.registerIcon("cctags:icon-background");

        for (Map.Entry<String, Icon> entry : tagIcons.entrySet())
            if (entry.getValue() == null) {
                String name = entry.getKey();
                Icon icon = registry.registerIcon("cctags:icon-" + name);
                entry.setValue(icon);
            }
    }

    public Icon getIcon(String name) {
        return tagIcons.get(name);
    }
}
