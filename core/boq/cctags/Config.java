package boq.cctags;

import net.minecraftforge.common.Configuration;

public class Config {
    public final String category = "gameplay";

    public final int PRINTER_USES_PER_INKSACK;
    public final int MAX_PRINTER_CAPACITY;
    public final int DEFAULT_LIB_TAG_COLOR;

    public final int MAX_BITMAP_TAG_SIZE;
    public final int MAX_CRAFTABLE_BITMAP_TAG_SIZE;

    public final int MAX_TEXT_TAG_SIZE;
    public final int MAX_CRAFTABLE_TEXT_TAG_SIZE;

    public Config(Configuration config) {
        PRINTER_USES_PER_INKSACK = config.get(category, "usesPerInksack", 10).getInt();
        MAX_PRINTER_CAPACITY = config.get(category, "maxPrinterCapacity", 100).getInt();
        DEFAULT_LIB_TAG_COLOR = config.get(category, "defaultLibTagColor", Constants.COLOR_BLUE).getInt();

        MAX_BITMAP_TAG_SIZE = config.get(category, "maxBitmapTagSize", 32, "Maximum number of pixels on custom bitmap tag").getInt();
        MAX_CRAFTABLE_BITMAP_TAG_SIZE = config.get(category, "maxCraftableBitmapTagSize", 16, "Maximum number of pixels on custom bitmap tag that can be used by player").getInt();

        MAX_TEXT_TAG_SIZE = config.get(category, "maxTextTagSize", 32, "Maximum length of custom tag text").getInt();
        MAX_CRAFTABLE_TEXT_TAG_SIZE = config.get(category, "maxCraftableTextTagSize", 16, "Maximum length of custom tag text that can be used by player").getInt();
    }
}
