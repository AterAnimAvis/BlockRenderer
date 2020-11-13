package com.unascribed.blockrenderer.fabric.client.varia;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface Identifiers {

    static ResourceLocation get(Item value) {
        return Registry.ITEM.getKey(value);
    }

}
