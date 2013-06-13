package boq.cctags.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import boq.cctags.TagData;
import boq.cctags.world.EntityTag;
import boq.utils.render.*;

public class EntityTagRenderer extends Render {

    private static final double Z_FIGHTER = -0.0001;

    private static void drawRectangle(Tessellator tes, double xm, double ym, double xp, double yp, Icon icon) {
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

    private final static ParameterModel<String> iconModels = new ParameterModel<String>() {

        @Override
        public void compile(String param) {
            final TagIcons icons = TagIcons.instance;
            GL11.glDisable(GL11.GL_CULL_FACE);

            Tessellator tes = new Tessellator();
            tes.setColorOpaque(255, 255, 255);
            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, 2 * Z_FIGHTER);
            drawRectangle(tes, 0.25, 0.25, 0.75, 0.75, icons.iconMarker);
            tes.draw();

            GL11.glColor3d(1, 1, 1);

            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, 2 * Z_FIGHTER);
            drawRectangle(tes, 0.25, 0.25, 0.75, 0.75, icons.iconBackground);

            Icon front = icons.getIcon(param);

            if (front != null) {
                tes.setTranslation(0, 0, 3 * Z_FIGHTER);
                drawRectangle(tes, 0.75, 0.25, 0.25, 0.75, front);
            }

            tes.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    };

    public void doRender(EntityTag entity, double x, double y, double z, float yaw, float partialTickTime) {
        BoxHelper.bindItemsTexture();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        final TagData data = entity.data;
        final ForgeDirection side = data.side;

        RotationHelper.setupSide(side);
        RotationHelper.setupRotation(data.rotation);

        BoxHelper.setColor(data.color);

        BoxHelper.setLightmapForBlock(renderManager.worldObj, (int)entity.posX + side.offsetX, (int)entity.posY + side.offsetY, (int)entity.posZ + side.offsetZ);
        iconModels.render(data.icon);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        doRender((EntityTag)entity, x, y, z, yaw, partialTickTime);
    }

}
