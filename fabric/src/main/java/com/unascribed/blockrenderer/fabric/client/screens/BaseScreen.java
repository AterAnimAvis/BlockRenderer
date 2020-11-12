package com.unascribed.blockrenderer.fabric.client.screens;

import com.unascribed.blockrenderer.fabric.client.screens.widgets.UpdateableSliderWidget;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.options.ExtendedSliderPercentageOption;
import com.unascribed.blockrenderer.varia.Maths;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("NotNullFieldNotInitialized")
public abstract class BaseScreen extends Screen {

    public static final int MIN_SIZE = 16;
    public static final int THRESHOLD = 32;
    public static final int MAX_SIZE = 2048;

    @Nullable
    protected final Screen old;

    protected double size = 512;
    protected UpdateableSliderWidget slider;
    protected ButtonWidget renderButton;

    public BaseScreen(Text title, @Nullable Screen old) {
        super(title);
        this.old = old;
    }

    @Override
    protected void init() {
        assert client != null;
        boolean enabled = enabled();
        addButton(new ButtonWidget(width / 2 - 100, height / 6 + 120, 98, 20, new TranslatableText("gui.cancel"), button -> client.openScreen(old)));

        renderButton = addButton(new ButtonWidget(width / 2 + 2, height / 6 + 120, 98, 20, new TranslatableText("block_renderer.gui.render"), this::onRender), enabled);

        size = MathHelper.clamp(size, getMinSize(), getMaxSize());

        ExtendedSliderPercentageOption option = new ExtendedSliderPercentageOption("block_renderer.gui.renderSize", getMinSize(), getMaxSize(), 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
        slider = addButton(new UpdateableSliderWidget(client.options, width / 2 - 100, height / 6 + 80, 200, 20, option), enabled);
    }

    protected boolean enabled() {
        assert client != null;
        return client.world != null;
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
        assert client != null;

        renderBackground(stack);

        super.render(stack, mouseX, mouseY, partialTicks);

        drawCenteredText(stack, client.textRenderer, title, width / 2, height / 6, -1);

        for (AbstractButtonWidget widget : buttons)
            if (widget.isHovered())
                widget.renderToolTip(stack, mouseX, mouseY);

        if (client.world != null) return;

        drawCenteredText(stack, client.textRenderer, new TranslatableText("block_renderer.gui.noWorld"), width / 2, height / 6 + 30, 0xFF5555);
    }

    protected abstract void onRender(ButtonWidget button);

    public Text getSliderDisplay(GameOptions settings, ExtendedSliderPercentageOption option) {
        int px = round(size);
        return option.getDisplayPrefix().copy().append(": " + px + "x" + px);
    }

    protected <T extends AbstractButtonWidget> T addButton(T button, boolean active) {
        addButton(button);

        button.active = active;
        button.visible = active;

        return button;
    }

    @Override
    public void onClose() {
        assert client != null;

        client.openScreen(old);
    }

}
