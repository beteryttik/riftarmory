package com.riftarmory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RiftSpearItem extends SwordItem {
    private static final int COST = 40;
    private static final int COOLDOWN = 600; // 30 сек

    public RiftSpearItem(ToolMaterial material, Settings settings) {
        super(material, 3.0f, -2.8f, settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld server) {
            if (user.getItemCooldownManager().isCoolingDown(stack)) {
                return ActionResult.FAIL;
            }
            if (Stability.get(stack) < 20) {
                return ActionResult.FAIL;
            }
            HitResult hit = player.raycast(20.0, 1.0f, false);
            BlockPos point;
            if (hit instanceof BlockHitResult bh && hit.getType() == HitResult.Type.BLOCK) {
                point = bh.getBlockPos().up();
            } else {
                point = BlockPos.ofFloored(hit.getPos());
            }
            if (!Stability.trySpend(stack, COST)) {
                Stability.add(stack, -20); // копьё воткнулось мимо — половинный штраф
            }
            user.getItemCooldownManager().set(stack, COOLDOWN);
            TntHelper.spawnTnt(server, point, 1);
            TntHelper.spawnTnt(server, point.east(), 1);
            TntHelper.spawnTnt(server, point.west(), 1);
            server.playSound(null, point, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.2f);
        }
        return ActionResult.SUCCESS;
    }
}
