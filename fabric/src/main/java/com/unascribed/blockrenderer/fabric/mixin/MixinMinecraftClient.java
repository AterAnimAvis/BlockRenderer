package com.unascribed.blockrenderer.fabric.mixin;

import com.unascribed.blockrenderer.fabric.client.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(method = "runTick(Z)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;popPush(Ljava/lang/String;)V", args = "ldc=gameRenderer"), allow = 1)
    public void hookGameRenderer(CallbackInfo ci) {
        ClientProxy.onFrameStart();
    }

}
