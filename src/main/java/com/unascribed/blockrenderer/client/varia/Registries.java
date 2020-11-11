package com.unascribed.blockrenderer.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public interface Registries {

    RegistryObject<Item> MAP = RegistryObject.of(new ResourceLocation("minecraft:filled_map"), ForgeRegistries.ITEMS);
    RegistryObject<Item> DISPENSER = RegistryObject.of(new ResourceLocation("minecraft:dispenser"), ForgeRegistries.ITEMS);
    RegistryObject<Item> CUTTER = RegistryObject.of(new ResourceLocation("minecraft:stonecutter"), ForgeRegistries.ITEMS);

    static void clazzLoad() {
        // INTENTIONAL LEFT BLANK
    }
}