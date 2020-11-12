package com.unascribed.blockrenderer.fabric.mixin.accessor;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface IHoveredSlot {

    @Accessor(value = "focusedSlot")
    @Nullable
    Slot block_renderer$accessor$hoveredSlot();

}
