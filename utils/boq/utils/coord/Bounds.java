package boq.utils.coord;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class Bounds {
    public double minX;
    public double minY;
    public double minZ;

    public double maxX;
    public double maxY;
    public double maxZ;

    public Bounds() {}

    public Bounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void reset() {
        minX = minY = minZ = 0;
        maxX = maxY = maxZ = 0;
    }

    public Bounds copy() {
        Bounds tmp = new Bounds();
        tmp.maxX = maxX;
        tmp.maxY = maxY;
        tmp.maxZ = maxZ;

        tmp.minX = minX;
        tmp.minY = minY;
        tmp.minZ = minZ;

        return tmp;
    }

    public void copyFrom(Bounds other) {
        maxX = other.maxX;
        maxY = other.maxY;
        maxZ = other.maxZ;

        minX = other.minX;
        minY = other.minY;
        minZ = other.minZ;
    }

    public void move(double x, double y, double z) {
        maxX += x;
        minX += x;

        maxY += y;
        minY += y;

        maxZ += z;
        minZ += z;
    }

    public void add(double x, double y, double z) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        minZ = Math.min(minZ, z);

        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        maxZ = Math.max(maxZ, z);
    }

    public void add(Bounds other) {
        minX = Math.min(minX, other.minX);
        minY = Math.min(minY, other.minY);
        minZ = Math.min(minZ, other.minZ);

        maxX = Math.max(maxX, other.maxX);
        maxY = Math.max(maxY, other.maxY);
        maxZ = Math.max(maxZ, other.maxZ);
    }

    public Vec3 getMin() {
        return Vec3.createVectorHelper(minX, minY, minZ);
    }

    public Vec3 getMax() {
        return Vec3.createVectorHelper(maxX, maxY, maxZ);
    }

    public Vec3 getDimensions() {
        return Vec3.createVectorHelper(maxX - minX, maxY - minY, maxZ - minZ);
    }

    public Vec3 getCenter() {
        return Vec3.createVectorHelper((maxX + minX) / 2.0, (maxY + minY) / 2.0, (maxZ + minZ) / 2.0);
    }

    public AxisAlignedBB toAABB(int x, int y, int z) {
        return AxisAlignedBB.getAABBPool().getAABB(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(maxX);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxY);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxZ);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minX);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minY);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minZ);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof Bounds) {
            Bounds other = (Bounds)obj;
            return (maxX == other.maxX) &&
                    (maxY == other.maxY) &&
                    (maxZ == other.maxZ) &&
                    (minX == other.minX) &&
                    (minY == other.minY) &&
                    (minZ == other.minZ);
        }
        return false;
    }

    public Bounds setMin(double x, double y, double z) {
        minX = x;
        minY = y;
        minZ = z;
        return this;
    }

    public Bounds setMax(double x, double y, double z) {
        maxX = x;
        maxY = y;
        maxZ = z;
        return this;
    }

    public void correct() {
        if (minX > maxX) {
            double tmp = maxX;
            maxX = minX;
            minX = tmp;
        }

        if (minY > maxY) {
            double tmp = maxY;
            maxY = minY;
            minY = tmp;
        }

        if (minZ > maxZ) {
            double tmp = maxZ;
            maxZ = minZ;
            minZ = tmp;
        }
    }
}
