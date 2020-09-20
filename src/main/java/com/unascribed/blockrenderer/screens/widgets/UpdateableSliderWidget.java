package com.unascribed.blockrenderer.screens.widgets;

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
        sliderValue = option.normalizeValue(desired);

        /* mcp: applyValue */
        func_230972_a_();

        /* mcp: updateMessage */
        func_230979_b_();
    }

}
