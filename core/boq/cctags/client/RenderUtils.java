package boq.cctags.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderUtils {

    public final static double TEXT_MARGIN = 0.1;

    public static void drawRectangle(Tessellator tes, double xm, double ym, double xp, double yp, Icon icon) {
        final double um = icon.getMinU();
        final double up = icon.getMaxU();

        final double vm = icon.getMinV();
        final double vp = icon.getMaxV();

        // v and y are reversed
        tes.addVertexWithUV(xm, ym, 0, um, vp);
        tes.addVertexWithUV(xp, ym, 0, up, vp);
        tes.addVertexWithUV(xp, yp, 0, up, vm);
        tes.addVertexWithUV(xm, yp, 0, um, vm);
    }

    public static void drawRectangle(Tessellator tes, double xm, double ym, double xp, double yp) {
        tes.addVertex(xm, ym, 0);
        tes.addVertex(xp, ym, 0);
        tes.addVertex(xp, yp, 0);
        tes.addVertex(xm, yp, 0);
    }

    public static void renderScaledText(Iterable<String> lines, double xm, double ym, double xp, double yp, double z) {
        final FontRenderer fr = FMLClientHandler.instance().getClient().fontRenderer;

        int lineCount = 0;
        int width = 0;
        for (String line : lines) {
            lineCount++;
            width = Math.max(width, fr.getStringWidth(line));
        }

        int height = fr.FONT_HEIGHT * lineCount;

        double dx = xp - xm;
        double dy = yp - ym;

        double scale;
        if (width > height)
            scale = dx / (width * (1 + TEXT_MARGIN));
        else
            scale = dy / (height * (1 + TEXT_MARGIN));

        double scaledX = width * scale;
        double scaledY = height * scale;

        double posX = xm + (dx - scaledX) / 2;
        double posY = yp - (dy - scaledY) / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)z);
        GL11.glScalef((float)scale, -(float)scale, 1);

        int y = 0;
        for (String s : lines) {
            fr.drawString(s, 0, y, 0);
            y += fr.FONT_HEIGHT;
        }
        GL11.glPopMatrix();
    }

    public static void renderBits(Tessellator tes, Iterable<String> lines, double xm, double ym, double xp, double yp, double z) {
        int maxWidth = 0;
        int height = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, line.length());
            height++;
        }

        int size = Math.max(maxWidth, height);

        double sizeX = xp - xm;
        double sizeY = yp - ym;

        double dx = sizeX / size;
        double dy = sizeY / size;

        double offsetX = (sizeX - dx * maxWidth) / 2;
        double offsetY = (sizeY - dy * height) / 2;

        double x = xm + offsetX;
        double y = ym + offsetY;

        tes.startDrawingQuads();
        tes.setNormal(0.0F, 0.0F, -1.0F);
        tes.setTranslation(0, 0, z);
        tes.setColorOpaque(0, 0, 0);

        for (String line : lines) {
            for (char c : line.toCharArray()) {
                if (c != ' ')
                    RenderUtils.drawRectangle(tes, x, y, x + dx, y + dy);

                x += dx;
            }

            y += dy;
            x = xm;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tes.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
