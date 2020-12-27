package com.unascribed.blockrenderer.client.impl.js;

import com.unascribed.blockrenderer.client.api.DefaultState;
import com.unascribed.blockrenderer.client.api.DefaultStateProvider;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.Marker;

import javax.annotation.Nullable;
import java.util.List;

import static com.unascribed.blockrenderer.Reference.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JavascriptDefaultStateProvider extends AbstractJavascriptProvider<DefaultStateProvider> {

    static final JavascriptDefaultStateProvider INSTANCE = new JavascriptDefaultStateProvider();

    @Override
    Marker marker() {
        return Markers.STATE;
    }

    @Override
    String type() {
        return "DefaultStateProvider";
    }

    @Override
    String fileType() {
        return ".state.js";
    }

    @Nullable
    @Override
    DefaultStateProvider cast(NashornScriptEngine engine) {
        return engine.getInterface(DefaultStateProvider.class);
    }

    @Override
    void removeProviders(List<DefaultStateProvider> providers) {
        DefaultState.remove(providers);
    }

    @Override
    void addProviders(List<DefaultStateProvider> providers) {
        DefaultState.add(providers);
    }

    @SubscribeEvent
    public static void onClientStart(FMLClientSetupEvent event) {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener((IResourceManagerReloadListener) manager -> INSTANCE.reload());
        INSTANCE.reload();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ForgeEventBus {

        @SubscribeEvent
        public static void addListeners(AddReloadListenerEvent event) {
            event.addListener((IResourceManagerReloadListener) manager -> INSTANCE.reload());
            INSTANCE.reload();
        }

    }

}
