package boq.cctags.tag;

import net.minecraft.util.Icon;

public enum TagSize {
    TAG_BROKEN("cctags:tag-broken", 0, "\u00A7kKAPUT\u00A7r") {
        @Override
        public boolean check(String contents) {
            return false;
        }

        @Override
        public boolean visible() {
            return false;
        }
    },
    TAG_4G("cctags:tag-inf", -1, "Almost unlimited") {
        @Override
        public boolean check(String contents) {
            return true;
        }
    },
    TAG_64("cctags:tag-64", 64, "64"),
    TAG_256("cctags:tag-256", 256, "256"),
    TAG_1K("cctags:tag-1k", 1024, "1k"),
    TAG_4K("cctags:tag-4k", 4096, "4k");

    public final String iconId;
    Icon icon;

    public final int size;
    public final String name;

    private TagSize(String iconId, int size, String name) {
        this.iconId = iconId;
        this.size = size;
        this.name = name;
    }

    public boolean check(String contents) {
        return contents.length() <= size;
    }

    public boolean visible() {
        return true;
    }
}
