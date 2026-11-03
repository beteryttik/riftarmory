package com.riftarmory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class Stability {
    public static final int MAX = 100;

    private Stability() {}

    public static int get(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, CustomData.DEFAULT);
        NbtCompound tag = data.copyTag();
        return tag.contains("rift_stability") ? tag.getInt("rift_stability") : MAX;
    }

    public static void add(ItemStack stack, int delta) {
        CustomData data = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, CustomData.DEFAULT);
        NbtCompound tag = data.copyTag();
        int v = (tag.contains("rift_stability") ? tag.getInt("rift_stability") : MAX) + delta;
        tag.putInt("rift_stability", Math.max(0, Math.min(MAX, v)));
        stack.set(DataComponentTypes.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean trySpend(ItemStack stack, int cost) {
        if (get(stack) < cost) return false;
        add(stack, -cost);
        return true;
    }
}
