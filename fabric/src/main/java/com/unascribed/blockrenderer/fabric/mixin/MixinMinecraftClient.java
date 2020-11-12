package com.unascribed.blockrenderer.fabric.mixin;

import com.unascribed.blockrenderer.fabric.client.proxy.ClientProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Redirect(method = "render(Z)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=gameRenderer"), allow = 1)
    public void hookGameRenderer(Profiler profiler, String arg) {
        profiler.swap(arg);

        //--------------------------------------------------------------------------------------------------------------

        ClientProxy.onFrameStart();
    }

}
