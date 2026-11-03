package com.riftarmory;

import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class LightningMaceItem extends SwordItem {
    private static final int COST = 40;

    public LightningMaceItem(ToolMaterial material, Settings settings) {
        super(material, 4.0f, -2.6f, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayerEntity player && target.getWorld() instanceof ServerWorld world) {
            if (Stability.trySpend(stack, COST)) {
                float[] factors = {1.0f, 0.75f, 0.5f};
                LivingEntity current = target;
                List<LivingEntity> visited = new java.util.ArrayList<>();
                visited.add(target);
                for (int jump = 1; jump < 3; jump++) {
                    Box area = new Box(current.getBlockPos()).expand(8.0);
                    List<LivingEntity> others = world.getEntitiesByClass(LivingEntity.class, area,
                            e -> e.isAlive() && e != player && !visited.contains(e));
                    if (others.isEmpty()) break;
                    LivingEntity next = others.stream()
                            .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(current)))
                            .get();
                    visited.add(next);
                    current = next;
                }
                BlockPos origin = target.getBlockPos();
                for (int i = 0; i < visited.size(); i++) {
                    LivingEntity victim = visited.get(i);
                    float dmg = 8.0f * factors[i];
                    victim.damage(world, world.getDamageSources().playerAttack(player), dmg);
                    world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                            victim.getX(), victim.getY() + 1.0, victim.getZ(), 12, 0.4, 0.6, 0.4, 0.05);
                }
                world.playSound(null, origin, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.6f, 1.5f);

                // В дождь молния бьёт и по тебе
                if (world.hasRain(target.getBlockPos())) {
                    player.damage(world, world.getDamageSources().lightningBolt(), 4.0f);
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
