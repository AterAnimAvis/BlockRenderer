package com.unascribed.blockrenderer.mixin.accessor;

import net.minecraft.client.options.Option;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Option.class)
public interface IDisplayPrefix {

    @Accessor(value = "key")
    Text getDisplayPrefix();

}
