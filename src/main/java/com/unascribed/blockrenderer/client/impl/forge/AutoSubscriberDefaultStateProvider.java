package com.unascribed.blockrenderer.client.impl.forge;

import com.unascribed.blockrenderer.client.api.DefaultState;
import com.unascribed.blockrenderer.client.api.DefaultStateProvider;
import com.unascribed.blockrenderer.client.varia.logging.Log;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class AutoSubscriberDefaultStateProvider implements DefaultStateProvider {

    @Nullable
    private static List<DefaultStateProvider> providers = null;

    static void load() {
        List<Object> subscribers = AutoSubscribers.subscribers;

        if (subscribers == null) {
            Log.error(Markers.BOUNDS, "Trying to load @AutoSubscriber DefaultStateProviders before 'AutoSubscribers' has collected them.");
            return;
        }

        providers = subscribers
                .stream()
                .filter(o -> DefaultStateProvider.class.isAssignableFrom(o.getClass()))
                .map(o -> (DefaultStateProvider) o)
                .collect(Collectors.toList());
    }

    @Override
    public DefaultState state(Entity entity, DefaultState current) {
        if (providers == null) load();
        if (providers == null) return current;

        for (DefaultStateProvider provider : providers) {
            if (provider.validFor(entity)) current = provider.state(entity, current);
        }

        return current;
    }

    @Override
    public boolean validFor(Entity entity) {
        return true;
    }

}
