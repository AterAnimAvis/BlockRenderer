package com.unascribed.blockrenderer.fabric.client.varia;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Registries {

    LazyLoadedValue<Item> MAP = new LazyLoadedValue<>(() -> lookupItem(new ResourceLocation("minecraft:filled_map")));
    LazyLoadedValue<Item> DISPENSER = new LazyLoadedValue<>(() -> lookupItem(new ResourceLocation("minecraft:dispenser")));
    LazyLoadedValue<Item> CUTTER = new LazyLoadedValue<>(() -> lookupItem(new ResourceLocation("minecraft:stonecutter")));

    static Item lookupItem(ResourceLocation identifier) {
        return Registry.ITEM.get(identifier);
    }

    static <A, B> Supplier<B> mapLazy(LazyLoadedValue<A> lazy, Function<A, B> mapper) {
        return () -> mapper.apply(lazy.get());
    }

}
