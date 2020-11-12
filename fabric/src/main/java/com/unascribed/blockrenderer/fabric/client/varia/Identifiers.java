package com.unascribed.blockrenderer.fabric.client.varia;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface Identifiers {

    static Identifier get(Item value) {
        return Registry.ITEM.getId(value);
    }

}
