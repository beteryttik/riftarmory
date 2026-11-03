package com.riftarmory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class RiftMaceItem extends SwordItem {
    private static final int COST = 60;

    public RiftMaceItem(ToolMaterial material, Settings settings) {
        super(material, 4.0f, -2.6f, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayerEntity player && target.getWorld() instanceof ServerWorld world) {
            if (Stability.trySpend(stack, COST)) {
                BlockPos base = target.getBlockPos();
                Random r = world.getRandom();
                int placed = 0;
                for (int i = 0; i < 40 && placed < 7; i++) {
                    int dx = r.nextInt(5) - 2, dy = r.nextInt(3), dz = r.nextInt(5) - 2;
                    BlockPos p = base.add(dx, dy, dz);
                    if (world.isAir(p)) {
                        TntHelper.spawnTnt(world, p, 1);
                        placed++;
                    }
                }
                world.playSound(null, base, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 0.8f);
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
