package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.unascribed.blockrenderer.fabric.client.render.Requests;
import com.unascribed.blockrenderer.fabric.client.render.manager.RenderManager;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.fabric.client.varia.Strings.translate;

@SuppressWarnings("NotNullFieldNotInitialized")
public class RenderAnimatedScreen extends EnterSizeScreen {

    private static final TranslationTextComponent TITLE = translate("block_renderer.gui.renderAnimated");

    private CheckboxButton asZip;
    private CheckboxButton autoLoop;

    private TextFieldWidget length;

    private boolean isInteger;

    public RenderAnimatedScreen(@Nullable Screen old, ItemStack stack) {
        super(TITLE, old, stack, false);
    }

    @Override
    public void init() {
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(true);
        boolean enabled = enabled();

        asZip = addButton(new HoverableCheckboxWidget(this, width / 2 - 100, height / 6 + 166, 98, 20, translate("block_renderer.gui.zip"), translate("block_renderer.gui.zip.tooltip"), false), enabled);
        autoLoop = addButton(new HoverableCheckboxWidget(this, width / 2 + 2, height / 6 + 166, 98, 20, translate("block_renderer.gui.loop"), translate("block_renderer.gui.loop.tooltip"), false), enabled);

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String prefill = (length == null ? "20" : length.getText());

        length = addButton(new HoverableTextFieldWidget(this, minecraft.fontRenderer, width / 2 - 100, height / 6 + 74, 200, 20, translate("block_renderer.gui.animatedLength"), translate("block_renderer.gui.animatedLength.tooltip")), enabled);
        length.setResponder((value) -> {
            isInteger = false;
            try {
                int v = Integer.parseInt(value);
                if (v > 0) isInteger = true;
            } catch (NumberFormatException ignore) {
            }
        });
        length.setText(prefill);
        addListener(length);
        setFocusedDefault(length);

        super.init();
    }

    @Override
    public void tick() {
        super.tick();
        length.tick();
        renderButton.visible = isInteger;
    }

    @Override
    public void closeScreen() {
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(false);
        super.closeScreen();
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.displayGuiScreen(old);
        if (minecraft.world == null) return;

        RenderManager.push(Requests.animated(stack, round(size), useId.isChecked(), addSize.isChecked(), Integer.parseInt(length.getText()), autoLoop.isChecked(), asZip.isChecked()));
    }

}
