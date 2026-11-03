package com.riftarmory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DestroyStickItem extends Item {
    public DestroyStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld server) {
            MinecraftServer mcServer = server.getServer();
            mcServer.getPlayerManager().broadcast(
                    Text.literal("§4Сервер будет уничтожен..."), false);

            BlockPos center = player.getBlockPos().up(40);
            int count = 0;
            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -8; dz <= 8; dz++) {
                    BlockPos p = center.add(dx, 0, dz);
                    if (server.isPosLoaded(p)) {
                        TntHelper.spawnTnt(server, p, 1);
                        count++;
                    }
                }
            }

            server.playSound(null, center, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.5f, 0.5f);

            // Одноразовая
            ItemStack stack = user.getStackInHand(hand);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            player.sendMessage(Text.literal("§4Уничтожено " + count + " блоков ТНТ. " + "Уничтожит этот сервер к черту."));
        }
        return ActionResult.SUCCESS;
    }
}
