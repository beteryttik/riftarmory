package com.riftarmory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class DragonMaceItem extends SwordItem {
    public DragonMaceItem(ToolMaterial material, Settings settings) {
        super(material, 4.0f, -2.6f, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(5);
        return super.postHit(stack, target, attacker);
    }
}
