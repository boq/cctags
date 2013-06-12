package boq.cctags.world;

import net.minecraft.util.Icon;

public enum TagSize {
    TAG_BROKEN(false, "cctags:tag-broken", 0, "\u00A7kKAPUT\u00A7r"),
    TAG_4G(true, "cctags:tag-inf", -1, "Almost unlimited"),
    TAG_64(true, "cctags:tag-64", 64, "64"),
    TAG_256(true, "cctags:tag-256", 256, "256"),
    TAG_1K(true, "cctags:tag-1k", 1024, "1k"),
    TAG_4K(true, "cctags:tag-4k", 4096, "4k");

    public final boolean visible;
    public final String iconId;
    Icon icon;

    public final int size;
    public final String name;

    private TagSize(boolean visible, String iconId, int size, String name) {
        this.visible = visible;
        this.iconId = iconId;
        this.size = size;
        this.name = name;
    }
}
