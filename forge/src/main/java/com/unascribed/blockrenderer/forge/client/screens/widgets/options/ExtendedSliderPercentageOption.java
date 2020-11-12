package com.unascribed.blockrenderer.forge.client.screens.widgets.options;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExtendedSliderPercentageOption extends SliderPercentageOption {

    public ExtendedSliderPercentageOption(String translationKey, double minValueIn, double maxValueIn, float stepSizeIn, Function<GameSettings, Double> getter, BiConsumer<GameSettings, Double> setter, BiFunction<GameSettings, ExtendedSliderPercentageOption, ITextComponent> getDisplayString) {
        super(translationKey, minValueIn, maxValueIn, stepSizeIn, getter, setter, (settings, option) -> getDisplayString.apply(settings, (ExtendedSliderPercentageOption) option));
    }

    public ITextComponent getDisplayPrefix() {
        return super.getBaseMessageTranslation();
    }
}
