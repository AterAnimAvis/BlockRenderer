package com.unascribed.blockrenderer.forge.client.screens.widgets;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.settings.SliderPercentageOption;

public class UpdateableSliderWidget extends OptionSlider {

    private final SliderPercentageOption option;

    public UpdateableSliderWidget(GameSettings settings, int x, int y, int width, int height, SliderPercentageOption option) {
        super(settings, x, y, width, height, option);
        this.option = option;
    }

    public void update(double desired) {
        value = option.toPct(desired);
        applyValue();
        updateMessage();
    }

}
