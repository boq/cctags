package boq.cctags.tag;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum TagType {
    NORMAL("item.cctag.normal", "cctags:tag-background"),
    GLASS("item.cctag.glass", "cctags:tag-background-glass"),
    BIG("item.cctag.big", "cctags:tag-background-big");

    public final String unlocalizedName;
    private final String backgroundName;
    public Icon backgroundIcon;

    private TagType(String unlocalizedName, String backgroundName) {
        this.unlocalizedName = unlocalizedName;
        this.backgroundName = backgroundName;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        backgroundIcon = registry.registerIcon(backgroundName);
    }

    public final static TagType[] VALUES = TagType.values();
}
