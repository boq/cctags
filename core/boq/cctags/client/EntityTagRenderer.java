package boq.cctags.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import boq.cctags.world.EntityTag;
import boq.utils.render.BoxHelper;
import boq.utils.render.ParameterModel;

public class EntityTagRenderer extends Render {

    private static final double Z_FIGHTER = 0.0001;

    private static void drawRectangle(Tessellator tes, double xm, double ym, double xp, double yp, Icon icon) {
        final double um = icon.getMinU();
        final double vm = icon.getMinV();
        final double up = icon.getMaxU();
        final double vp = icon.getMaxV();

        tes.addVertexWithUV(xm, ym, 0, um, vm);
        tes.addVertexWithUV(xp, ym, 0, up, vm);
        tes.addVertexWithUV(xp, yp, 0, up, vp);
        tes.addVertexWithUV(xm, yp, 0, um, vp);
    }

    private final static ParameterModel<String> iconModels = new ParameterModel<String>() {

        @Override
        public void compile(String param) {
            final TagIcons icons = TagIcons.instance;
            GL11.glDisable(GL11.GL_CULL_FACE);

            Tessellator tes = new Tessellator();
            tes.setColorOpaque(255, 255, 255);
            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, 3 * Z_FIGHTER);
            drawRectangle(tes, 0.25, 0.25, 0.75, 0.75, icons.iconMarker);
            tes.draw();

            GL11.glColor3d(1, 1, 1);

            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, Z_FIGHTER);
            drawRectangle(tes, 0.25, 0.25, 0.75, 0.75, icons.iconBackground);

            Icon front = icons.getIcon(param);

            if (front != null) {
                tes.setTranslation(0, 0, 2 * Z_FIGHTER);
                drawRectangle(tes, 0.25, 0.25, 0.75, 0.75, front);
            }

            tes.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    };

    public void doRender(EntityTag entity, double x, double y, double z, float yaw, float partialTickTime) {
        BoxHelper.bindItemsTexture();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        BoxHelper.setColor(entity.data.color);
        BoxHelper.setLightmapForBlock(renderManager.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
        iconModels.compile(entity.data.icon);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        doRender((EntityTag)entity, x, y, z, yaw, partialTickTime);
    }

}
