package com.riftarmory;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public final class ModItems {
    private ModItems() {}

    // Простые материалы
    public static final Item DIAMOND_ROD = register("diamond_rod", Item::new,
            new Item.Settings().maxCount(64));
    // Сердца комплектов: remainder выдаёт копьё вместе с булавой
    public static final Item SPEAR_RIFT = register("spear_rift", settings -> new RiftSpearItem(ToolMaterial.DIAMOND, settings),
            maceSettings());
    public static final Item RIFT_FRAGMENT = register("rift_fragment", Item::new,
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).recipeRemainder(SPEAR_RIFT));
    public static final Item VOID_SHARD = register("void_shard", Item::new,
            new Item.Settings().maxCount(16).rarity(Rarity.RARE));

    public static final Item SPEAR_DRAGON = register("spear_dragon", settings -> new DragonSpearItem(ToolMaterial.DIAMOND, settings),
            maceSettings());
    public static final Item DRAGON_HEART = register("dragon_heart", Item::new,
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).recipeRemainder(SPEAR_DRAGON));

    public static final Item MACE_LIGHTNING = register("mace_lightning", settings -> new LightningMaceItem(ToolMaterial.DIAMOND, settings),
            maceSettings());
    public static final Item LIGHTNING_CHARGE = register("lightning_charge", Item::new,
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public static final Item SPEAR_GHOST = register("spear_ghost", settings -> new GhostSpearItem(ToolMaterial.DIAMOND, settings),
            maceSettings());
    public static final Item GHOST_GLASS = register("ghost_glass", Item::new,
            new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).recipeRemainder(SPEAR_GHOST));

    public static final Item MACE_RIFT = register("mace_rift", settings -> new RiftMaceItem(ToolMaterial.DIAMOND, settings),
            maceSettings());
    public static final Item MACE_DRAGON = register("mace_dragon", settings -> new DragonMaceItem(ToolMaterial.DIAMOND, settings),
            maceSettings());
    public static final Item MACE_GHOST = register("mace_ghost", settings -> new GhostMaceItem(ToolMaterial.DIAMOND, settings),
            maceSettings());

    public static final Item DESTROY_STICK = register("destroy_stick", DestroyStickItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());

    private static Item.Settings maceSettings() {
        return new Item.Settings().maxCount(1).rarity(Rarity.RARE);
    }

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(RiftArmory.MOD_ID, name));
        return Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
    }
}
