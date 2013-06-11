package boq.cctags.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import boq.utils.render.BoxHelper;
import boq.utils.render.ParameterModel;

public class EntityTagRenderer extends Render {

    private final static ParameterModel<String> iconModels = new ParameterModel<String>() {

        @Override
        public void compile(String param) {
            // Icon icon = CCTags.instance.itemTag.getIcon(param);
            Tessellator tes = new Tessellator();
            tes.setColorOpaque(255, 255, 255);
            tes.startDrawingQuads();
            tes.setTranslation(0.25, 0, 0.25);

            tes.setNormal(0.0F, 0.0F, -1.0F);
            tes.addVertex(0, 0, 0);
            tes.addVertex(0.5, 0, 0);
            tes.addVertex(0.5, 0.5, 0);
            tes.addVertex(0, 0.5, 0);

            // tes.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
            // tes.addVertexWithUV(0.5, 0, 0, icon.getMaxU(), icon.getMinV());
            // tes.addVertexWithUV(0.5, 0.5, 0, icon.getMaxU(), icon.getMaxV());
            // tes.addVertexWithUV(0, 0.5, 0, icon.getMinU(), icon.getMaxV());
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tes.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    };

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        BoxHelper.bindItemsTexture();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glColor3d(1, 1, 1);

        int l = renderManager.worldObj.getLightBrightnessForSkyBlocks((int)entity.posX, (int)entity.posY, (int)entity.posZ, 0);
        int i1 = l % 65536;
        int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1, j1);

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        iconModels.render("hello");
        GL11.glPopMatrix();
    }

}
