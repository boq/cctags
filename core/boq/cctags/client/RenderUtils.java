package boq.cctags.client;

import java.util.List;

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

    public static void renderScaledText(List<String> lines, double xm, double ym, double xp, double yp, double z) {
        final FontRenderer fr = FMLClientHandler.instance().getClient().fontRenderer;
        int height = fr.FONT_HEIGHT * lines.size();

        int width = 0;
        for (String line : lines)
            width = Math.max(width, fr.getStringWidth(line));

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

}
