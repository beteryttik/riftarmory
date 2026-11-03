package com.riftarmory;

import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class TntHelper {
    private TntHelper() {}

    /** Спавнит мгновенное ТНТ с коротким фузом. */
    public static void spawnTnt(ServerWorld world, BlockPos pos, int fuse) {
        TntEntity tnt = new TntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
        tnt.setFuse(fuse);
        world.spawnEntity(tnt);
    }
}
