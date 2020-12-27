package com.unascribed.blockrenderer.client.api;

import net.minecraft.entity.Entity;

public interface DefaultStateProvider {

    DefaultState state(Entity entity, DefaultState current);

    boolean validFor(Entity entity);

}
