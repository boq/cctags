package boq.cctags.tag;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import boq.cctags.CCTags;
import boq.cctags.client.RenderUtils;

import com.google.common.base.Splitter;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

        @Override
        public String getDescription(LanguageRegistry reg, String argument) {
            return reg.getStringLocalization("cctag.icon.none");
        }

    },
    PREDEFINED {

        @Override
        public boolean canBeCrafted(String argument) {
            return true;
        }

        @Override
        public boolean validate(String argument) {
            return TagIcons.instance.predefinedIcons.containsKey(argument);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, z);

            Icon icon = TagIcons.instance.predefinedIcons.get(argument);
            RenderUtils.drawRectangle(tes, xm, ym, xp, yp, icon);

            tes.draw();
        }

        @Override
        public String getDescription(LanguageRegistry reg, String argument) {
            final String format = reg.getStringLocalization("cctag.icon.predefined");
            return String.format(format, argument);
        }

    },
    BITMAP {
        @Override
        public boolean canBeCrafted(String argument) {
            return calculateLength(argument) <= CCTags.config.MAX_CRAFTABLE_BITMAP_TAG_SIZE;
        }

        @Override
        public boolean validate(String argument) {
            return calculateLength(argument) <= CCTags.config.MAX_CRAFTABLE_BITMAP_TAG_SIZE;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
            Iterable<String> lines = Splitter.on('_').split(argument);
            RenderUtils.renderBits(tes, lines, xm, ym, xp, yp, z);
        }

        @Override
        public String getDescription(LanguageRegistry reg, String argument) {
            return reg.getStringLocalization("cctag.icon.bitmap");
        }

    },
    TEXT {
        @Override
        public boolean canBeCrafted(String argument) {
            return calculateLength(argument) <= CCTags.config.MAX_CRAFTABLE_TEXT_TAG_SIZE;
        }

        @Override
        public boolean validate(String argument) {
            return calculateLength(argument) <= CCTags.config.MAX_TEXT_TAG_SIZE;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
            Iterable<String> split = Splitter.on('_').split(argument);
            RenderUtils.renderScaledText(split, xm, ym, xp, yp, z);
        }

        @Override
        public String getDescription(LanguageRegistry reg, String argument) {
            final String format = reg.getStringLocalization("cctag.icon.text");
            return String.format(format, argument);
        }

    };

    private static int calculateLength(String input) {
        int result = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != '_' && c != ' ')
                result++;
        }

        return result;
    }

    public abstract boolean canBeCrafted(String argument);

    public abstract boolean validate(String argument);

    @SideOnly(Side.CLIENT)
    public abstract void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument);

    public abstract String getDescription(LanguageRegistry reg, String argument);
}
