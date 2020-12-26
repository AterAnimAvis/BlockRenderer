package com.unascribed.blockrenderer.fabric.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Registries {

    Lazy<Item> CUTTER = new Lazy<>(() -> lookupItem(new Identifier("minecraft:stonecutter")));
    Lazy<Item> DISPENSER = new Lazy<>(() -> lookupItem(new Identifier("minecraft:dispenser")));
    Lazy<Item> EMPTY_MAP = new Lazy<>(() -> lookupItem(new Identifier("minecraft:map")));
    Lazy<Item> MAP = new Lazy<>(() -> lookupItem(new Identifier("minecraft:filled_map")));
    Lazy<Item> PATTERN = new Lazy<>(() -> lookupItem(new Identifier("minecraft:mojang_banner_pattern")));

    static Item lookupItem(Identifier identifier) {
        return Registry.ITEM.get(identifier);
    }

    static <A, B> Supplier<B> mapLazy(Lazy<A> lazy, Function<A, B> mapper) {
        return () -> mapper.apply(lazy.get());
    }

}
