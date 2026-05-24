package com.bartek.intellium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public final class IntelliumCuller {
    private static final ThreadLocal<BlockPos.MutableBlockPos> PROBE_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    private static final double OCCLUSION_VERTICAL_DELTA = 14.0D;
    private static final double OCCLUSION_HORIZONTAL_RADIUS_SQ = 18.0D * 18.0D;
    private static final int OCCLUSION_CEILING_BUFFER = 10;

    private IntelliumCuller() {
    }

    public static boolean shouldRenderEntity(Entity entity, Frustum frustum, double camX, double camY, double camZ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (entity == minecraft.player) {
            return true;
        }

        double ex = entity.getX();
        double ey = entity.getY();
        double ez = entity.getZ();

        double dx = ex - camX;
        double dy = ey - camY;
        double dz = ez - camZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > IntelliumProfile.maxEntityDistanceSq) {
            return false;
        }

        AABB box = entity.getBoundingBox();
        float margin = IntelliumProfile.frustumMargin;

        double minX = box.minX - margin;
        double minY = box.minY - margin;
        double minZ = box.minZ - margin;
        double maxX = box.maxX + margin;
        double maxY = box.maxY + margin;
        double maxZ = box.maxZ + margin;

        if (maxX <= minX) {
            double center = (box.minX + box.maxX) * 0.5D;
            minX = center - 0.0005D;
            maxX = center + 0.0005D;
        }
        if (maxY <= minY) {
            double center = (box.minY + box.maxY) * 0.5D;
            minY = center - 0.0005D;
            maxY = center + 0.0005D;
        }
        if (maxZ <= minZ) {
            double center = (box.minZ + box.maxZ) * 0.5D;
            minZ = center - 0.0005D;
            maxZ = center + 0.0005D;
        }

        if (!frustum.isVisible(new AABB(minX, minY, minZ, maxX, maxY, maxZ))) {
            return false;
        }

        if (!IntelliumProfile.enableAggressiveOcclusion) {
            return true;
        }

        if (dy > -OCCLUSION_VERTICAL_DELTA) {
            return true;
        }

        double horizontalSq = dx * dx + dz * dz;
        if (horizontalSq > OCCLUSION_HORIZONTAL_RADIUS_SQ) {
            return true;
        }

        Level level = entity.level();
        int blockX = (int) Math.floor(ex);
        int blockY = (int) Math.floor(ey);
        int blockZ = (int) Math.floor(ez);

        int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);
        if (topY <= blockY + OCCLUSION_CEILING_BUFFER) {
            return true;
        }

        BlockPos.MutableBlockPos pos = PROBE_POS.get();
        pos.set(blockX, blockY + 1, blockZ);
        return level.canSeeSky(pos);
    }
}
