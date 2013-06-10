package boq.utils.render;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

public final class BoxHelper {
    private BoxHelper() {}

    public static final EnumSet<ForgeDirection> allSides = EnumSet.allOf(ForgeDirection.class);
    public static final EnumSet<ForgeDirection> wallsOnly = EnumSet.range(ForgeDirection.NORTH, ForgeDirection.EAST);
    public static final EnumSet<ForgeDirection> noFloor = EnumSet.range(ForgeDirection.UP, ForgeDirection.EAST);

    private static final double X[] = new double[] { 0, 0, 1, 1, 0 };
    private static final double Z[] = new double[] { 0, 1, 1, 0, 0 };
    private static final float NX[] = new float[] { -1, 0, +1, 0 };
    private static final float NZ[] = new float[] { 0, +1, 0, -1 };
    private static final ForgeDirection dirs[] = new ForgeDirection[] { ForgeDirection.WEST, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.NORTH };

    public static void drawBox(Tessellator tes, double xSize, double ySize, double zSize, EnumSet<ForgeDirection> visible) {
        if (visible.contains(ForgeDirection.UP)) {
            tes.setNormal(0, 1, 0);

            tes.addVertex(0, ySize, 0);
            tes.addVertex(0, ySize, zSize);
            tes.addVertex(xSize, ySize, zSize);
            tes.addVertex(xSize, ySize, 0);
        }

        if (visible.contains(ForgeDirection.DOWN)) {
            tes.setNormal(0, -1, 0);

            tes.addVertex(0, 0, 0);
            tes.addVertex(xSize, 0, 0);
            tes.addVertex(xSize, 0, zSize);
            tes.addVertex(0, 0, zSize);
        }

        for (int i = 0; i < 4; i++) {
            ForgeDirection dir = dirs[i];
            if (!visible.contains(dir))
                continue;
            tes.setNormal(NX[i], 0, NZ[i]);
            double x1 = xSize * X[i];
            double z1 = zSize * Z[i];
            double x2 = xSize * X[i + 1];
            double z2 = zSize * Z[i + 1];
            tes.addVertex(x1, ySize, z1);
            tes.addVertex(x1, 0, z1);
            tes.addVertex(x2, 0, z2);
            tes.addVertex(x2, ySize, z2);
        }
    }

    public static void drawBox(Tessellator tes, double xSize, double ySize, double zSize, EnumSet<ForgeDirection> visible, SideMap<?> texture) {
        if (visible.contains(ForgeDirection.UP)) {
            Icon tex = texture.get(ForgeDirection.UP);

            tes.setNormal(0, 1, 0);
            tes.addVertexWithUV(0, ySize, 0, tex.getMinU(), tex.getMinV());
            tes.addVertexWithUV(0, ySize, zSize, tex.getMinU(), tex.getMaxV());
            tes.addVertexWithUV(xSize, ySize, zSize, tex.getMaxU(), tex.getMaxV());
            tes.addVertexWithUV(xSize, ySize, 0, tex.getMaxU(), tex.getMinV());
        }

        if (visible.contains(ForgeDirection.DOWN)) {
            Icon tex = texture.get(ForgeDirection.DOWN);

            tes.setNormal(0, -1, 0);
            tes.addVertexWithUV(0, 0, 0, tex.getMinU(), tex.getMinV());
            tes.addVertexWithUV(xSize, 0, 0, tex.getMaxU(), tex.getMinV());
            tes.addVertexWithUV(xSize, 0, zSize, tex.getMaxU(), tex.getMaxV());
            tes.addVertexWithUV(0, 0, zSize, tex.getMinU(), tex.getMaxV());
        }

        for (int i = 0; i < 4; i++) {
            ForgeDirection dir = dirs[i];
            if (!visible.contains(dir))
                continue;
            tes.setNormal(NX[i], 0, NZ[i]);
            double x1 = xSize * X[i];
            double z1 = zSize * Z[i];
            double x2 = xSize * X[i + 1];
            double z2 = zSize * Z[i + 1];

            Icon tex = texture.get(dir);

            tes.addVertexWithUV(x1, ySize, z1, tex.getMinU(), tex.getMinV());
            tes.addVertexWithUV(x1, 0, z1, tex.getMinU(), tex.getMaxV());
            tes.addVertexWithUV(x2, 0, z2, tex.getMaxU(), tex.getMaxV());
            tes.addVertexWithUV(x2, ySize, z2, tex.getMaxU(), tex.getMinV());
        }
    }

    public static Vec3 worldToPlayer(Vec3 vec) {
        double rx = vec.xCoord - TileEntityRenderer.staticPlayerX;
        double ry = vec.yCoord - TileEntityRenderer.staticPlayerY;
        double rz = vec.zCoord - TileEntityRenderer.staticPlayerZ;
        return Vec3.createVectorHelper(rx, ry, rz);
    }

    public static Icon getBlockIcon(String name) {
        return getRender().textureMapBlocks.getTextureExtry(name);
    }

    public static Icon getItemIcon(String name) {
        return getRender().textureMapItems.getTextureExtry(name);
    }

    public static RenderEngine getRender() {
        return Minecraft.getMinecraft().renderEngine;
    }

    public static void bindItemsTexture() {
        getRender().bindTexture("/gui/items.png");
    }

    public static void bindBlocksTexture() {
        getRender().bindTexture("/terrain.png");
    }

    public static void bindTexture(String texture) {
        getRender().bindTexture(texture);
    }
}
