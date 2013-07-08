package boq.cctags.tag;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import boq.cctags.CCTags;
import boq.cctags.client.RenderUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

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

    },
    BITMAP {
        @Override
        public boolean canBeCrafted(String argument) {
            final int limit = CCTags.config.MAX_CRAFTABLE_BITMAP_TAG_SIZE;
            return argument.length() < limit * limit;
        }

        @Override
        public boolean validate(String argument) {
            int length = 0;

            for (char c : argument.toCharArray()) {
                if (c == '_')
                    continue;

                if (c != '1' && c != '0')
                    return false;

                length++;
            }

            return TagIcons.isSquare(length, CCTags.config.MAX_BITMAP_TAG_SIZE);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
            int len = argument.length();
            double sq = Math.sqrt(len);

            double dx = (xp - xm) / sq;
            double dy = (yp - ym) / sq;

            double x = xm;
            double y = ym;

            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, z);
            tes.setColorOpaque(0, 0, 0);

            for (char c : argument.toCharArray()) {
                if (c == '1')
                    RenderUtils.drawRectangle(tes, x, y, x + dx, y + dy);

                x += dx;
                if (x >= xp) {
                    y += dy;
                    x = xm;
                }
            }

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tes.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

    },
    TEXT {
        @Override
        public boolean canBeCrafted(String argument) {
            return argument.length() <= CCTags.config.MAX_CRAFTABLE_TEXT_TAG_SIZE;
        }

        @Override
        public boolean validate(String argument) {
            return argument.length() <= CCTags.config.MAX_TEXT_TAG_SIZE;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument) {
            List<String> split = Lists.newArrayList(Splitter.on('_').split(argument));
            RenderUtils.renderScaledText(split, xm, ym, xp, yp, z);
        }
    };

    public abstract boolean canBeCrafted(String argument);

    public abstract boolean validate(String argument);

    @SideOnly(Side.CLIENT)
    public abstract void render(Tessellator tes, double xm, double ym, double xp, double yp, double z, String argument);
}
