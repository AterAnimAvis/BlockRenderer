package com.unascribed.blockrenderer.forge.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public interface Registries {

    RegistryObject<Item> CUTTER = RegistryObject.of(new ResourceLocation("minecraft:stonecutter"), ForgeRegistries.ITEMS);
    RegistryObject<Item> DISPENSER = RegistryObject.of(new ResourceLocation("minecraft:dispenser"), ForgeRegistries.ITEMS);
    RegistryObject<Item> EMPTY_MAP = RegistryObject.of(new ResourceLocation("minecraft:map"), ForgeRegistries.ITEMS);
    RegistryObject<Item> MAP = RegistryObject.of(new ResourceLocation("minecraft:filled_map"), ForgeRegistries.ITEMS);
    RegistryObject<Item> PATTERN = RegistryObject.of(new ResourceLocation("minecraft:mojang_banner_pattern"), ForgeRegistries.ITEMS);

    static void clazzLoad() {
        // INTENTIONAL LEFT BLANK
    }
}
