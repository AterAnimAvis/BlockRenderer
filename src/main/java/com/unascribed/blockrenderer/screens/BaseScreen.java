package com.unascribed.blockrenderer.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public abstract class BaseScreen extends Screen {

    private static final int MIN_SIZE = 16;
    private static final int MAX_SIZE = 2048;

    @Nullable
    protected final Screen old;

    protected double size = 512;

    protected DoubleOptionSliderWidget slider;

    public BaseScreen(Text title, @Nullable Screen old) {
        super(title);
        this.old = old;
    }

    @Override
    public void init() {
        assert client != null;
        boolean enabled = client.world != null;

        addButton(new ButtonWidget(width/2-100, height/6+120, 98, 20, new TranslatableText("gui.cancel"), button -> client.openScreen(old)));

        addButton(new ButtonWidget(width/2+2, height/6+120, 98, 20, new TranslatableText("blockrenderer.gui.render"), this::onRender), enabled);

        size = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

        DoubleOption option = new DoubleOption("blockrenderer.gui.renderSize", MIN_SIZE, MAX_SIZE, 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
        slider = addButton(new DoubleOptionSliderWidget(client.options, width/2-100, height/6+80, 200, 20, option), enabled);
    }

    protected int round(double value) {
        assert client != null;

        int val = (int)value;

        // There's a more efficient method in MathHelper, but it rounds up. We want the nearest.
        int nearestPowerOfTwo = (int)Math.pow(2, Math.ceil(Math.log(val)/Math.log(2)));

        if (nearestPowerOfTwo < MAX_SIZE && Math.abs(val-nearestPowerOfTwo) < 32) val = nearestPowerOfTwo;

        return MathHelper.clamp(val, MIN_SIZE, MAX_SIZE);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        assert client != null;

        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, partialTicks);

        drawCenteredString(matrices, client.textRenderer, title.getString(), width/2, height/6, -1);

        if (client.world != null) return;

        drawCenteredString(matrices, client.textRenderer, I18n.translate("blockrenderer.gui.noWorld"), width/2, height/6+30, 0xFF5555);
    }

    abstract void onRender(AbstractButtonWidget button);

    public MutableText getSliderDisplay(GameOptions settings, DoubleOption option) {
        int px = round(size);
        return option.getDisplayPrefix().append(px + "x" + px);
    }

    protected <T extends AbstractButtonWidget> T addButton(T button, boolean active) {
        addButton(button);

        button.active = active;
        button.visible = active;

        return button;
    }

}
