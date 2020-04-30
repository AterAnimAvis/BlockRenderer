package com.unascribed.blockrenderer.mixin.accessor;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(RecipeBookWidget.class)
public interface ISearchField {

    @Accessor
    @Nullable
    TextFieldWidget getSearchField();

}
