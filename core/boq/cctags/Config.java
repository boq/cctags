package boq.cctags;

import net.minecraftforge.common.Configuration;

public class Config {
    public final static String CATEGORY_GAMEPLAY = "gameplay";
    public final static String CATEGORY_INVENTORY = "inventory";

    public final int PRINTER_USES_PER_INKSACK;
    public final int MAX_PRINTER_CAPACITY;
    public final int DEFAULT_LIB_TAG_COLOR;

    public final int MAX_BITMAP_TAG_SIZE;
    public final int MAX_CRAFTABLE_BITMAP_TAG_SIZE;

    public final int MAX_TEXT_TAG_SIZE;
    public final int MAX_CRAFTABLE_TEXT_TAG_SIZE;

    public final boolean SHOW_LIBRARY;
    public final boolean ADD_OWN_TURTLES;

    public Config(Configuration config) {
        PRINTER_USES_PER_INKSACK = config.get(CATEGORY_GAMEPLAY, "usesPerInksack", 10).getInt();
        MAX_PRINTER_CAPACITY = config.get(CATEGORY_GAMEPLAY, "maxPrinterCapacity", 100).getInt();
        DEFAULT_LIB_TAG_COLOR = config.get(CATEGORY_GAMEPLAY, "defaultLibTagColor", Constants.COLOR_BLUE).getInt();

        MAX_BITMAP_TAG_SIZE = config.get(CATEGORY_GAMEPLAY, "maxBitmapTagSize", 32, "Maximum number of pixels on custom bitmap tag").getInt();
        MAX_CRAFTABLE_BITMAP_TAG_SIZE = config.get(CATEGORY_GAMEPLAY, "maxCraftableBitmapTagSize", 16, "Maximum number of pixels on custom bitmap tag that can be used by player").getInt();

        MAX_TEXT_TAG_SIZE = config.get(CATEGORY_GAMEPLAY, "maxTextTagSize", 32, "Maximum length of custom tag text").getInt();
        MAX_CRAFTABLE_TEXT_TAG_SIZE = config.get(CATEGORY_GAMEPLAY, "maxCraftableTextTagSize", 16, "Maximum length of custom tag text that can be used by player").getInt();

        SHOW_LIBRARY = config.get(CATEGORY_INVENTORY, "showLibraryInCreative", true).getBoolean(true);
        ADD_OWN_TURTLES = config.get(CATEGORY_INVENTORY, "addOwnTurtles", true, "Should turtles with new peripherals be visible in creative mode").getBoolean(true);
    }
}
