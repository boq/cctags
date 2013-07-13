package boq.cctags.tag;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum TagType {
    NORMAL("item.cctag.normal", true, "cctags:tag-background"),
    GLASS("item.cctag.glass", true, "cctags:tag-background-glass"),
    BIG("item.cctag.big", true, "cctags:tag-background-big");

    public final boolean visible;
    public final String unlocalizedName;
    private final String backgroundName;
    public Icon backgroundIcon;

    private TagType(String name, boolean visible, String backgroundName) {
        unlocalizedName = name;
        this.visible = visible;
        this.backgroundName = backgroundName;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        backgroundIcon = registry.registerIcon(backgroundName);
    }

    public final static TagType[] VALUES = TagType.values();
}
