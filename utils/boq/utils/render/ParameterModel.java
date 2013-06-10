package boq.utils.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.GLAllocation;

import org.lwjgl.opengl.GL11;

public abstract class ParameterModel<T> {
    private Map<T, Integer> lists = new HashMap<T, Integer>();

    public abstract void compile(T param);

    public void render(T param) {
        Integer displayList = lists.get(param);
        if (displayList == null) {
            displayList = GLAllocation.generateDisplayLists(1);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glNewList(displayList, GL11.GL_COMPILE);
            compile(param);
            GL11.glEndList();
            GL11.glPopMatrix();
            lists.put(param, displayList);
        }
        GL11.glCallList(displayList);
    }

    @Override
    protected void finalize() throws Throwable {
        for (Integer list : lists.values())
            GL11.glDeleteLists(list, 1);
    }

}
