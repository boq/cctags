package boq.cctags;

import net.minecraftforge.common.Configuration;

public class Config {
    public final String category = "gameplay";

    public final int PRINTER_USES_PER_INKSACK;
    public final int MAX_PRINTER_CAPACITY;

    public Config(Configuration config) {
        PRINTER_USES_PER_INKSACK = config.get(category, "usesPerInksack", 10).getInt();
        MAX_PRINTER_CAPACITY = config.get(category, "maxPrinterCapacity", 100).getInt();
    }
}
