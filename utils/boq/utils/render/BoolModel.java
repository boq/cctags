package boq.utils.render;

import net.minecraft.client.renderer.GLAllocation;

import org.lwjgl.opengl.GL11;

public abstract class BoolModel {

    protected int displayListTrue;
    protected boolean compiledTrue;

    protected int displayListFalse;
    protected boolean compiledFalse;

    public abstract void compile(boolean value);

    private int renderOption(boolean param) {
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        int displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        compile(param);
        GL11.glEndList();
        GL11.glPopMatrix();
        return displayList;
    }

    public void render(boolean param) {
        if (param) {
            if (!compiledTrue) {
                displayListTrue = renderOption(true);
                compiledTrue = true;
            }
            GL11.glCallList(displayListTrue);
        } else {
            if (!compiledFalse) {
                displayListFalse = renderOption(false);
                compiledFalse = true;
            }
            GL11.glCallList(displayListFalse);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (compiledTrue)
            GL11.glDeleteLists(displayListTrue, 1);

        if (compiledFalse)
            GL11.glDeleteLists(displayListFalse, 1);
    }

}
