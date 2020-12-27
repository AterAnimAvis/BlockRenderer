package com.unascribed.blockrenderer.client.impl.forge;

import com.unascribed.blockrenderer.client.api.Bounds;
import com.unascribed.blockrenderer.client.api.BoundsProvider;
import com.unascribed.blockrenderer.client.varia.logging.Log;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import net.minecraft.entity.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class AutoSubscriberBoundsProvider implements BoundsProvider {

    private static List<BoundsProvider> providers = null;

    static void load() {
        List<Object> subscribers = AutoSubscribers.subscribers;

        if (subscribers == null) {
            Log.error(Markers.BOUNDS, "Trying to load @AutoSubscriber BoundsProviders before 'AutoSubscribers' has collected them.");
            return;
        }

        providers = subscribers
                .stream()
                .filter(o -> BoundsProvider.class.isAssignableFrom(o.getClass()))
                .map(o -> (BoundsProvider) o)
                .collect(Collectors.toList());
    }

    @Override
    public Bounds bounds(Entity entity, Bounds current) {
        if (providers == null) load();
        if (providers == null) return current;

        for (BoundsProvider provider : providers) {
            if (provider.validFor(entity)) current = provider.bounds(entity, current);
        }

        return current;
    }

    @Override
    public boolean validFor(Entity entity) {
        return true;
    }

}
