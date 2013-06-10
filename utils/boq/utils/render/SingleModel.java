package boq.utils.render;

import net.minecraft.client.renderer.GLAllocation;

import org.lwjgl.opengl.GL11;

public abstract class SingleModel {

    protected int displayList;
    protected boolean compiled;

    public abstract void compile();

    public void render() {
        if (!compiled) {
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            displayList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(displayList, GL11.GL_COMPILE);
            compile();
            GL11.glEndList();
            GL11.glPopMatrix();
            compiled = true;
        }
        GL11.glCallList(displayList);
    }

    @Override
    protected void finalize() throws Throwable {
        if (compiled)
            GL11.glDeleteLists(displayList, 1);
    }

}
