package com.unascribed.blockrenderer.fabric.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface Identifiers {

    static ResourceLocation get(Item value) {
        return Registry.ITEM.getKey(value);
    }

}
