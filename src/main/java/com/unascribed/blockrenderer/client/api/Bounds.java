package com.unascribed.blockrenderer.client.api;

import com.unascribed.blockrenderer.client.api.extensions.ZeroVector3dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3d;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3dc;
import com.unascribed.blockrenderer.client.varia.logging.Log;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

public interface Bounds {

    Vector3dc getSize();

    Vector3dc getOffset();

    static boolean add(BoundsProvider provider) {
        return Internal.ADDITIONAL.add(provider);
    }

    static boolean remove(BoundsProvider provider) {
        return Internal.ADDITIONAL.remove(provider);
    }

    static boolean add(Collection<BoundsProvider> provider) {
        return Internal.ADDITIONAL.addAll(provider);
    }

    static boolean remove(Collection<BoundsProvider> provider) {
        return Internal.ADDITIONAL.removeAll(provider);
    }

    static void reloadServices() {
        Internal.LOADER.reload();
    }

    static Bounds forEntity(Entity entity) {
        AxisAlignedBB bb = entity.getBoundingBox();
        Bounds bounds = new Instance(new Vector3d(bb.getXSize(), bb.getYSize(), bb.getZSize()), new Vector3d(entity.getPosX() - bb.minX, entity.getPosY() - bb.minY, entity.getPosZ() - bb.minZ));

        for (BoundsProvider provider : Internal.LOADER) {
            Log.debug(Markers.BOUNDS, "LOADER: {}", provider.toString());

            try {
                if (provider.validFor(entity)) {
                    bounds = provider.bounds(entity, bounds);
                    Log.debug(Markers.BOUNDS, "> Resulting Bounds: {}", bounds);
                }
            } catch (Exception e) {
                Log.error(Markers.BOUNDS, "BoundsProvider errored:", e);
            }
        }

        for (BoundsProvider provider : Internal.ADDITIONAL) {
            Log.debug(Markers.BOUNDS, "ADDITIONAL: {}", provider);
            try {
                if (provider.validFor(entity)) {
                    bounds = provider.bounds(entity, bounds);
                    Log.debug(Markers.BOUNDS, "> Resulting Bounds: {}", bounds);
                }
            } catch (Exception e) {
                Log.error(Markers.BOUNDS, "BoundsProvider errored:", e);
            }
        }

        return bounds;
    }

    static Bounds of(double x, double y, double z) {
        return new Instance(x, y, z);
    }

    static Bounds of(double x, double y, double z, double ox, double oy, double oz) {
        return new Instance(x, y, z, ox, oy, oz);
    }

    static Bounds of(Vector3dc size) {
        return new Instance(size);
    }

    static Bounds of(Vector3dc size, Vector3dc offset) {
        return new Instance(size, offset);
    }

    class Instance implements Bounds {

        private final Vector3dc size;
        private final Vector3dc offset;

        public Instance(double x, double y, double z) {
            this(new Vector3d(x, y, z));
        }

        public Instance(double x, double y, double z, double ox, double oy, double oz) {
            this(new Vector3d(x, y, z), new Vector3d(ox, oy, oz));
        }

        public Instance(Vector3dc size) {
            this(size, ZeroVector3dc.INSTANCE);
        }

        public Instance(Vector3dc size, Vector3dc offset) {
            this.size = size;
            this.offset = offset;
        }

        @Override
        public Vector3dc getSize() {
            return size;
        }

        @Override
        public Vector3dc getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("size", size).append("offset", offset).toString();
        }
    }

    class Internal {
        private static final ServiceLoader<BoundsProvider> LOADER = ServiceLoader.load(BoundsProvider.class);

        private static final List<BoundsProvider> ADDITIONAL = new ArrayList<>();
    }

}
