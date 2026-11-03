package com.riftarmory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class GhostSpearItem extends SwordItem {
    private static final int COST = 45;
    private static final int COOLDOWN = 600;
    private static final double RANGE = 24.0;

    public GhostSpearItem(ToolMaterial material, Settings settings) {
        super(material, 3.0f, -2.8f, settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld server) {
            if (user.getItemCooldownManager().isCoolingDown(stack)) {
                return ActionResult.FAIL;
            }
            if (!Stability.trySpend(stack, COST)) {
                return ActionResult.FAIL;
            }
            user.getItemCooldownManager().set(stack, COOLDOWN);

            Vec3d start = player.getEyePos();
            Vec3d dir = player.getRotationVec(1.0f);

            // Ищем цель вдоль луча сквозь стены: сначала честный рейкаст по сущностям,
            // затем луч через блоки до 24 блоков с проверкой сущностей в узкой коробке.
            HitResult hit = player.raycast(RANGE, 1.0f, false);
            LivingEntity victim = null;
            if (hit instanceof EntityHitResult eh && eh.getEntity() instanceof LivingEntity le) {
                victim = le;
            } else {
                for (double d = 1.0; d <= RANGE && victim == null; d += 0.5) {
                    Vec3d p = start.add(dir.multiply(d));
                    var candidates = server.getOtherEntities(player,
                            new net.minecraft.util.math.Box(p, p).expand(0.9),
                            e -> e instanceof LivingEntity && e.isAlive());
                    if (!candidates.isEmpty()) {
                        victim = (LivingEntity) candidates.get(0);
                    }
                }
            }

            if (victim != null) {
                victim.damage(server, server.getDamageSources().playerAttack(player), 7.0f);
                server.spawnParticles(ParticleTypes.GLOW, victim.getX(), victim.getY() + 1.0, victim.getZ(),
                        15, 0.4, 0.6, 0.4, 0.02);
                server.playSound(null, victim.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE,
                        SoundCategory.PLAYERS, 1.0f, 0.6f);
                player.sendMessage(net.minecraft.text.Text.literal("§fКопьё призрака поразило цель сквозь стены."));
            } else {
                Stability.add(stack, 20); // копьё прошло мимо всех — щадящий штраф
                player.sendMessage(net.minecraft.text.Text.literal("§7Копьё рассеялось в пустоте..."));
            }
        }
        return ActionResult.SUCCESS;
    }
}
