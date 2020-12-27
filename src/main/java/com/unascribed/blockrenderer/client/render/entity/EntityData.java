package com.unascribed.blockrenderer.client.render.entity;

import net.minecraft.entity.Entity;

public class EntityData {

    public final Entity entity;
    public final float pitch;
    public final float yaw;
    public final int fixedAge;
    public final int scale;

    public EntityData(Entity entity, float pitch, float yaw, int fixedAge, int scale) {
        this.entity = entity;
        this.pitch = pitch;
        this.yaw = yaw;
        this.fixedAge = fixedAge;
        this.scale = scale;
    }

}
