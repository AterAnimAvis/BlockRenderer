package com.unascribed.blockrenderer.forge.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Registries {

    RegistryObject<Item> MAP = RegistryObject.of(new ResourceLocation("minecraft:filled_map"), ForgeRegistries.ITEMS);
    RegistryObject<Item> DISPENSER = RegistryObject.of(new ResourceLocation("minecraft:dispenser"), ForgeRegistries.ITEMS);
    RegistryObject<Item> CUTTER = RegistryObject.of(new ResourceLocation("minecraft:stonecutter"), ForgeRegistries.ITEMS);

    static void clazzLoad() {
        // INTENTIONAL LEFT BLANK
    }

    static <A extends Item, B> Supplier<B> mapLazy(RegistryObject<A> lazy, Function<A, B> mapper) {
        return () -> mapper.apply(lazy.get());
    }

}
