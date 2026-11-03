package com.riftarmory;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.world.World;

public class GhostMaceItem extends SwordItem {
    private static final int COST = 50;
    private static final int COOLDOWN = 700; // 35 сек

    public GhostMaceItem(ToolMaterial material, Settings settings) {
        super(material, 4.0f, -2.6f, settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld server) {
            if (user.getItemCooldownManager().isCoolingDown(stack) || !user.isSneaking()) {
                return ActionResult.FAIL;
            }
            if (!Stability.trySpend(stack, COST)) {
                return ActionResult.FAIL;
            }
            user.getItemCooldownManager().set(stack, COOLDOWN);

            // Фаза: 2 секунды максимальной устойчивости (приближение неосязаемости)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 4, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 40, 0, false, false));

            server.spawnParticles(ParticleTypes.POOF, player.getX(), player.getY() + 1.0, player.getZ(),
                    20, 0.5, 0.8, 0.5, 0.01);
            server.playSound(null, player.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1.0f, 0.7f);
            player.sendMessage(net.minecraft.text.Text.literal("§7Ты перешёл в фазу..."));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Лёгкое «фазовое» замедление цели
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, false, false));
        return super.postHit(stack, target, attacker);
    }
}
