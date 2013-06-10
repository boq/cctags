package boq.utils.lang;

import cpw.mods.fml.common.registry.LanguageRegistry;

public final class LangList {
    private LangList() {}

    public static class Lang {
        private String lang;

        public Lang(String lang) {
            this.lang = lang;
        }

        public void load(String resourcePrefix) {
            String fileName = resourcePrefix + lang + ".xml";
            LanguageRegistry.instance().loadLocalization(fileName, lang, true);
        }
    }

    public static Lang[] languages = new Lang[] {
            new Lang("en_US")
    };

    public static void loadAll(String resourcePrefix) {
        for (Lang l : languages)
            l.load(resourcePrefix);
    }

}
