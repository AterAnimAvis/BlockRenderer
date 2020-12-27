package com.unascribed.blockrenderer.client.render.entity;

import com.unascribed.blockrenderer.client.render.IRequest;
import com.unascribed.blockrenderer.client.render.request.RenderingRequest;
import com.unascribed.blockrenderer.client.varia.Files;
import net.minecraft.entity.Entity;

public class EntityRequest {

    public static IRequest single(Entity entity, int size) {
        DefaultEntityHandler handler = new DefaultEntityHandler(Files.DEFAULT_FOLDER, size, true, false, true);

        // The Pitch and Yaw Values are based off of https://minecraft.gamepedia.com/Minecraft_Wiki:Standardized_views#Entity_renders
        EntityData data = new EntityData(entity, 30f, -45f, 0, size);

        return new RenderingRequest<>(
                new EntityRenderer(),
                data,
                data,
                handler,
                handler
        );
    }

}
