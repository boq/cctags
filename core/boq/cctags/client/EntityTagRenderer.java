package boq.cctags.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import boq.cctags.tag.*;
import boq.cctags.tag.TagIcons.IconData;
import boq.utils.render.*;

public class EntityTagRenderer extends Render {

    private static final double Z_FIGHTER = 0.0005;

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
            RenderUtils.drawRectangle(tes, -0.25, -0.25, 0.25, 0.25, icons.iconMarker);
            tes.draw();

            GL11.glColor3d(1, 1, 1);

            tes.startDrawingQuads();
            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.setTranslation(0, 0, 2 * Z_FIGHTER);
            RenderUtils.drawRectangle(tes, -0.25, -0.25, 0.25, 0.25, icons.iconBackground);

            IconData render = TagIcons.parseIconString(param);

            if (render != null) {
                tes.setTranslation(0, 0, 3 * Z_FIGHTER);
                render.render(tes, -0.25, -0.25, 0.25, 0.25);
            }

            tes.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    };

    public void doRender(EntityTag entity, double x, double y, double z, float yaw, float partialTickTime) {
        BoxHelper.bindItemsTexture();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        if (RenderManager.field_85095_o) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
            renderOffsetAABB(entity.boundingBox, -entity.posX, -entity.posY, -entity.posZ);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        final TagData data = entity.data;
        final ForgeDirection side = data.side;

        CenterRotationHelper.setupSide(side);
        CenterRotationHelper.setupRotation(data.rotation);
        GL11.glTranslated(0, 0, 0.5);

        BoxHelper.setColor(data.color);

        iconModels.render(data.icon);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        doRender((EntityTag)entity, x, y, z, yaw, partialTickTime);
    }

}
