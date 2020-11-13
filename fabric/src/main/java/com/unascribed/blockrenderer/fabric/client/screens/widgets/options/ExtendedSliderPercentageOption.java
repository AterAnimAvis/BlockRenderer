package com.unascribed.blockrenderer.fabric.client.screens.widgets.options;

import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExtendedSliderPercentageOption extends ProgressOption {

    public ExtendedSliderPercentageOption(String translationKey, double minValueIn, double maxValueIn, float stepSizeIn, Function<Options, Double> getter, BiConsumer<Options, Double> setter, BiFunction<Options, ExtendedSliderPercentageOption, Component> getDisplayString) {
        super(translationKey, minValueIn, maxValueIn, stepSizeIn, getter, setter, (settings, option) -> getDisplayString.apply(settings, (ExtendedSliderPercentageOption) option));
    }

    public Component getDisplayPrefix() {
        return super.getCaption();
    }

}
