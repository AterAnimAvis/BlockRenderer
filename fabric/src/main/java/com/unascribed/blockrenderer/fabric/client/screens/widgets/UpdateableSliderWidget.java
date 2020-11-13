package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.SliderButton;

public class UpdateableSliderWidget extends SliderButton {

    private final ProgressOption option;

    public UpdateableSliderWidget(Options settings, int x, int y, int width, int height, ProgressOption option) {
        super(settings, x, y, width, height, option);
        this.option = option;
    }

    public void update(double desired) {
        value = option.toPct(desired);
        applyValue();
        updateMessage();
    }

}
