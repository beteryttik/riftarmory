package com.riftarmory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiftArmory implements ModInitializer {
    public static final String MOD_ID = "riftarmory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<ItemGroup> GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "main"));

    @Override
    public void onInitialize() {
        ModItems.register();
        Registry.register(Registries.ITEM_GROUP, GROUP, net.fabricmc.fabric.api.object.builder.v1.item.group.FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.riftarmory"))
                .icon(() -> new ItemStack(ModItems.MACE_RIFT))
                .build());

        ItemGroupEventsHook.register();
        ChestSpawner.register();
        LOGGER.info("Rift Armory loaded. Ready to destroy servers.");
    }

    private static class ItemGroupEventsHook {
        static void register() {
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(GROUP).register(entries -> {
                entries.add(ModItems.DIAMOND_ROD);
                entries.add(ModItems.RIFT_FRAGMENT);
                entries.add(ModItems.VOID_SHARD);
                entries.add(ModItems.DRAGON_HEART);
                entries.add(ModItems.GHOST_GLASS);
                entries.add(ModItems.LIGHTNING_CHARGE);
                entries.add(ModItems.MACE_RIFT);
                entries.add(ModItems.SPEAR_RIFT);
                entries.add(ModItems.MACE_DRAGON);
                entries.add(ModItems.SPEAR_DRAGON);
                entries.add(ModItems.MACE_LIGHTNING);
                entries.add(ModItems.MACE_GHOST);
                entries.add(ModItems.SPEAR_GHOST);
                entries.add(ModItems.DESTROY_STICK);
            });
        }
    }
}
