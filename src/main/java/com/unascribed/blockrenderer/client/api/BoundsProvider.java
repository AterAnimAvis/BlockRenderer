package com.unascribed.blockrenderer.client.api;

import net.minecraft.entity.Entity;

public interface BoundsProvider {

    Bounds bounds(Entity entity, Bounds current);

    boolean validFor(Entity entity);

}
