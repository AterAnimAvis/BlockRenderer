package com.unascribed.blockrenderer.forge.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.forge.client.screens.widgets.UpdateableSliderWidget;
import com.unascribed.blockrenderer.forge.client.screens.widgets.options.ExtendedSliderPercentageOption;
import com.unascribed.blockrenderer.varia.Maths;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static com.unascribed.blockrenderer.forge.client.varia.Strings.translate;

@SuppressWarnings("NotNullFieldNotInitialized")
public abstract class BaseScreen extends Screen {

    public static final int MIN_SIZE = 16;
    public static final int THRESHOLD = 32;
    public static final int MAX_SIZE = 2048;

    @Nullable
    protected final Screen old;

    protected double size = 512;
    protected UpdateableSliderWidget slider;
    protected Button renderButton;

    public BaseScreen(ITextComponent title, @Nullable Screen old) {
        super(title);
        this.old = old;
    }

    @Override
    protected void init() {
        assert minecraft != null;
        boolean enabled = enabled();
        addButton(new Button(width / 2 - 100, height / 6 + 120, 98, 20, translate("gui.cancel"), button -> minecraft.setScreen(old)));

        renderButton = addButton(new Button(width / 2 + 2, height / 6 + 120, 98, 20, translate("block_renderer.gui.render"), this::onRender), enabled);

        size = Maths.clamp(size, getMinSize(), getMaxSize());

        ExtendedSliderPercentageOption option = new ExtendedSliderPercentageOption("block_renderer.gui.renderSize", getMinSize(), getMaxSize(), 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
        slider = addButton(new UpdateableSliderWidget(minecraft.options, width / 2 - 100, height / 6 + 80, 200, 20, option), enabled);
    }

    protected boolean enabled() {
        assert minecraft != null;
        return minecraft.level != null;
    }

    protected int getMinSize() {
        return MIN_SIZE;
    }

    protected int getThreshold() {
        return THRESHOLD;
    }

    protected int getMaxSize() {
        return MAX_SIZE;
    }

    protected int round(double value) {
        return Maths.roundAndClamp(value, getMinSize(), getMaxSize(), getThreshold());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && renderButton.visible) {
            onRender(renderButton);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        renderBackground(stack);

        super.render(stack, mouseX, mouseY, partialTicks);

        drawCenteredString(stack, minecraft.font, title, width / 2, height / 6, -1);

        for (Widget widget : buttons)
            if (widget.isHovered())
                widget.renderToolTip(stack, mouseX, mouseY);

        if (minecraft.level != null) return;

        drawCenteredString(stack, minecraft.font, translate("block_renderer.gui.noWorld"), width / 2, height / 6 + 30, 0xFF5555);
    }

    protected abstract void onRender(Button button);

    public ITextComponent getSliderDisplay(GameSettings settings, ExtendedSliderPercentageOption option) {
        int px = round(size);
        return option.getDisplayPrefix().copy().append(": " + px + "x" + px);
    }

    protected <T extends Widget> T addButton(T button, boolean active) {
        addButton(button);

        button.active = active;
        button.visible = active;

        return button;
    }

    @Override
    public void onClose() {
        assert minecraft != null;

        minecraft.setScreen(old);
    }

}
