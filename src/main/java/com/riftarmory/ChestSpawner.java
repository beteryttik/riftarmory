package com.riftarmory;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public final class ChestSpawner {
    private static final int INTERVAL = 20 * 60 * 20; // 20 минут
    private static long ticks = 0;
    private static final Random RNG = Random.create();

    private ChestSpawner() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticks++;
            if (ticks % INTERVAL != 0) return;
            if (server.getPlayerManager().getPlayerList().isEmpty()) return;

            ServerPlayerEntity player = server.getPlayerManager().getPlayerList()
                    .get(RNG.nextInt(server.getPlayerManager().getPlayerList().size()));
            ServerWorld world = player.getServerWorld();

            BlockPos pos = findSpot(world, player.getBlockPos());
            if (pos == null) return;

            world.setBlockState(pos, Blocks.CHEST.defaultBlockState());
            if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                boolean fragment = RNG.nextInt(100) < 70;
                ItemStack loot = fragment
                        ? new ItemStack(ModItems.RIFT_FRAGMENT, 1 + RNG.nextInt(2))
                        : new ItemStack(ModItems.VOID_SHARD, 1);
                chest.setStack(RNG.nextInt(chest.size()), loot);
                chest.markDirty();
            }
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.6f);
        });
    }

    private static BlockPos findSpot(ServerWorld world, BlockPos around) {
        for (int attempt = 0; attempt < 40; attempt++) {
            double ang = RNG.nextDouble() * Math.PI * 2;
            int dist = 200 + RNG.nextInt(100);
            int x = around.getX() + (int) (Math.cos(ang) * dist);
            int z = around.getZ() + (int) (Math.sin(ang) * dist);
            BlockPos top = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, new BlockPos(x, 0, z));
            BlockPos place = top.up();
            if (world.isAir(place) && !world.getBlockState(top).isOf(Blocks.WATER)) {
                return place;
            }
        }
        return null;
    }
}
