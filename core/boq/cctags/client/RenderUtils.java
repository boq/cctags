package boq.cctags.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

public class RenderUtils {

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

}
