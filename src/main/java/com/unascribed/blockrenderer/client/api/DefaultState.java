package com.unascribed.blockrenderer.client.api;

import com.unascribed.blockrenderer.client.varia.logging.Log;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import net.minecraft.entity.Entity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

public interface DefaultState {

    int fixedAge();

    static boolean add(DefaultStateProvider provider) {
        return Internal.ADDITIONAL.add(provider);
    }

    static boolean remove(DefaultStateProvider provider) {
        return Internal.ADDITIONAL.remove(provider);
    }

    static boolean add(Collection<DefaultStateProvider> provider) {
        return Internal.ADDITIONAL.addAll(provider);
    }

    static boolean remove(Collection<DefaultStateProvider> provider) {
        return Internal.ADDITIONAL.removeAll(provider);
    }

    static void reloadServices() {
        Internal.LOADER.reload();
    }

    static DefaultState forEntity(Entity entity) {
        DefaultState state = new Instance(0);

        for (DefaultStateProvider provider : Internal.LOADER) {
            Log.debug(Markers.STATE, "LOADER: {}", provider);
            try {
                if (provider.validFor(entity)) {
                    state = provider.state(entity, state);
                    Log.debug(Markers.STATE, "> Resulting State: {}", state);
                }
            } catch (Exception e) {
                Log.error(Markers.STATE, "BoundsProvider errored:", e);
            }
        }

        for (DefaultStateProvider provider : Internal.ADDITIONAL) {
            Log.debug(Markers.STATE, "ADDITIONAL: {}", provider);
            try {
                if (provider.validFor(entity)) {
                    state = provider.state(entity, state);
                    Log.debug(Markers.STATE, "> Resulting Bounds: {}", state);
                }
            } catch (Exception e) {
                Log.error(Markers.STATE, "BoundsProvider errored:", e);
            }
        }

        return state;
    }

    static DefaultState of(int fixedAge) {
        return new Instance(fixedAge);
    }

    class Instance implements DefaultState {

        private final int fixedAge;

        public Instance(int fixedAge) {
            this.fixedAge = fixedAge;
        }

        @Override
        public int fixedAge() {
            return fixedAge;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("fixedAge", fixedAge).toString();
        }
    }

    class Internal {
        private static final ServiceLoader<DefaultStateProvider> LOADER = ServiceLoader.load(DefaultStateProvider.class);

        private static final List<DefaultStateProvider> ADDITIONAL = new ArrayList<>();
    }

}
