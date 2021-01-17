package com.unascribed.blockrenderer.fabric.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Registries {

    LazyValue<Item> CUTTER = new LazyValue<>(() -> lookupItem(new ResourceLocation("minecraft:stonecutter")));
    LazyValue<Item> DISPENSER = new LazyValue<>(() -> lookupItem(new ResourceLocation("minecraft:dispenser")));
    LazyValue<Item> EMPTY_MAP = new LazyValue<>(() -> lookupItem(new ResourceLocation("minecraft:map")));
    LazyValue<Item> MAP = new LazyValue<>(() -> lookupItem(new ResourceLocation("minecraft:filled_map")));
    LazyValue<Item> PATTERN = new LazyValue<>(() -> lookupItem(new ResourceLocation("minecraft:mojang_banner_pattern")));

    static Item lookupItem(ResourceLocation identifier) {
        return Registry.ITEM.getOrDefault(identifier);
    }

    static <A, B> Supplier<B> mapLazy(LazyValue<A> lazy, Function<A, B> mapper) {
        return () -> mapper.apply(lazy.getValue());
    }

}
