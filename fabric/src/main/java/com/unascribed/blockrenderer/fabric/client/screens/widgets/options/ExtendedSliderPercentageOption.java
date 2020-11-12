package com.unascribed.blockrenderer.fabric.client.screens.widgets.options;

import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExtendedSliderPercentageOption extends DoubleOption {

    public ExtendedSliderPercentageOption(String translationKey, double minValueIn, double maxValueIn, float stepSizeIn, Function<GameOptions, Double> getter, BiConsumer<GameOptions, Double> setter, BiFunction<GameOptions, ExtendedSliderPercentageOption, Text> getDisplayString) {
        super(translationKey, minValueIn, maxValueIn, stepSizeIn, getter, setter, (settings, option) -> getDisplayString.apply(settings, (ExtendedSliderPercentageOption) option));
    }

    @Override
    public Text getDisplayPrefix() {
        return super.getDisplayPrefix();
    }

}
