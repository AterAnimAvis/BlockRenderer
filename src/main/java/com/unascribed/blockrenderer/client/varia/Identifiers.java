package com.unascribed.blockrenderer.client.varia;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface Identifiers {

    static ResourceLocation get(IForgeRegistryEntry<?> value) {
        ResourceLocation identifier = value.getRegistryName();

        if (identifier == null) return new ResourceLocation("air");

        return identifier;
    }

}
