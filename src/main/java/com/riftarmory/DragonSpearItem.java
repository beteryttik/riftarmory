package com.riftarmory;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class DragonSpearItem extends SwordItem {
    private static final int COST = 50;
    private static final int COOLDOWN = 600;
    private static final double RANGE = 12.0;

    public DragonSpearItem(ToolMaterial material, Settings settings) {
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

            BlockPos landing = findLanding(server, player);
            if (landing == null) {
                Stability.add(stack, 25); // вернуть половину — телепорт сорвался
                player.sendMessage(net.minecraft.text.Text.literal("§cРывок не удался: точка приземления опасна."));
                return ActionResult.FAIL;
            }

            player.teleport(server, landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5,
                    player.getYaw(), player.getPitch());

            // Кольцо драконьего пламени
            Box ring = new Box(landing).expand(2.0);
            server.getOtherEntities(player, ring, e -> e instanceof LivingEntity && e.isAlive())
                    .forEach(e -> {
                        LivingEntity living = (LivingEntity) e;
                        living.setOnFireFor(4);
                        living.damage(server, server.getDamageSources().playerAttack(player), 6.0f);
                    });

            server.spawnParticles(ParticleTypes.SOUL_FIRE, landing.getX() + 0.5, landing.getY() + 0.5,
                    landing.getZ() + 0.5, 40, 1.6, 0.2, 1.6, 0.02);
            server.playSound(null, landing, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 0.7f, 1.4f);
        }
        return ActionResult.SUCCESS;
    }

    private BlockPos findLanding(ServerWorld world, ServerPlayerEntity player) {
        HitResult hit = player.raycast(RANGE, 1.0f, true);
        BlockPos base = BlockPos.ofFloored(hit.getPos());
        // ищем безопасную точку: от базы вниз и рядом
        for (BlockPos candidate : new BlockPos[]{base, base.down(), base.up(), base.north(), base.south(), base.east(), base.west()}) {
            if (isSafe(world, candidate)) {
                return candidate;
            }
        }
        // ищем ближайший безопасный пол под базой
        BlockPos cursor = base;
        for (int i = 0; i < 12; i++) {
            if (isSafe(world, cursor)) {
                return cursor;
            }
            cursor = cursor.down();
        }
        return null;
    }

    private boolean isSafe(ServerWorld world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());
        BlockState at = world.getBlockState(pos);
        BlockState above = world.getBlockState(pos.up());
        boolean solid = !below.isAir() && below.isSolidBlock(world, pos.down());
        boolean free = at.isAir() && above.isAir();
        boolean fluid = below.isOf(Blocks.WATER) || below.isOf(Blocks.LAVA);
        return solid && free && !fluid;
    }
}
