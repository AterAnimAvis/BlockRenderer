package com.unascribed.blockrenderer.fabric.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface IHoveredSlot {

    @Accessor(value = "hoveredSlot")
    @Nullable
    Slot block_renderer$accessor$hoveredSlot();

}
