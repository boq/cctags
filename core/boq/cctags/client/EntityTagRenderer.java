package boq.cctags.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import boq.cctags.CCTags;
import boq.utils.render.BoxHelper;
import boq.utils.render.ParameterModel;

public class EntityTagRenderer extends Render {

    private final static ParameterModel<String> iconModels = new ParameterModel<String>() {

        @Override
        public void compile(String param) {
            Icon icon = CCTags.instance.itemTag.getIcon(param);
            Tessellator tes = new Tessellator();
            tes.startDrawingQuads();
            tes.setTranslation(0.25, 0, 0.25);
            tes.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
            tes.addVertexWithUV(0.5, 0, 0, icon.getMaxU(), icon.getMinV());
            tes.addVertexWithUV(0.5, 0.5, 0, icon.getMaxU(), icon.getMaxV());
            tes.addVertexWithUV(0, 0.5, 0, icon.getMinU(), icon.getMaxV());

            // tes.addVertexWithUV(0.5, 0, 0, icon.getMaxU(), icon.getMinV());
            // tes.addVertexWithUV(0, 0.5, 0, icon.getMinU(), icon.getMaxV());
            //
            tes.draw();
        }
    };

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        BoxHelper.bindItemsTexture();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        iconModels.compile("hello");
        GL11.glPopMatrix();
    }

}
