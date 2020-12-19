package com.unascribed.blockrenderer.forge.client.screens.item;

import com.unascribed.blockrenderer.forge.client.render.RenderManager;
import com.unascribed.blockrenderer.forge.client.render.Requests;
import com.unascribed.blockrenderer.forge.client.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.forge.client.screens.widgets.HoverableTextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.forge.client.varia.Strings.translate;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderAnimatedScreen extends EnterSizeScreen {

    private static final TranslationTextComponent TITLE = translate("block_renderer.gui.renderAnimated");

    private CheckboxButton autoLoop;

    private TextFieldWidget length;

    private boolean isInteger;

    public RenderAnimatedScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);
    }

    @Override
    public void init() {
        assert minecraft != null;
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        boolean enabled = enabled();

        super.init();

        autoLoop = addButton(new HoverableCheckboxWidget(this, width / 2 + 2, height / 6 + 166, 98, 20, translate("block_renderer.gui.loop"), translate("block_renderer.gui.loop.tooltip"), false), enabled);

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String prefill = (length == null ? "20" : length.getValue());

        length = addButton(new HoverableTextFieldWidget(this, minecraft.font, width / 2 - 100, height / 6 + 74, 200, 20, translate("block_renderer.gui.animatedLength"), translate("block_renderer.gui.animatedLength.tooltip")), enabled);
        length.setResponder((value) -> {
            isInteger = false;
            try {
                int v = Integer.parseInt(value);
                if (v > 0) isInteger = true;
            } catch (NumberFormatException ignore) {
            }
        });
        length.setValue(prefill);
        length.setCanLoseFocus(false);
        setInitialFocus(length);
    }

    @Override
    public void tick() {
        super.tick();
        length.tick();
        renderButton.visible = isInteger;
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (length.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (length.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.setScreen(old);
        if (minecraft.level == null) return;

        RenderManager.push(Requests.animated(stack, round(size), useId.selected(), addSize.selected(), Integer.parseInt(length.getValue()), autoLoop.selected()));
    }

}
