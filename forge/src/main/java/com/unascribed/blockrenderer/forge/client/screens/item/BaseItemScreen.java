package com.unascribed.blockrenderer.forge.client.screens.item;

import com.unascribed.blockrenderer.forge.client.screens.BaseScreen;
import com.unascribed.blockrenderer.forge.client.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.forge.client.screens.widgets.HoverableTinyButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

/*
 * Note: Screen's get initialized in init
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public abstract class BaseItemScreen extends BaseScreen {

    protected Button actualSize;
    protected CheckboxButton useId;
    protected CheckboxButton addSize;

    public BaseItemScreen(ITextComponent title, @Nullable Screen old) {
        super(title, old);
    }

    @Override
    public void init() {
        assert minecraft != null;
        boolean enabled = enabled();

        super.init();

        actualSize = addButton(new HoverableTinyButtonWidget(this, width / 2 + 104, height / 6 + 80, new TranslationTextComponent("block_renderer.gui.actualSize"), new TranslationTextComponent("block_renderer.gui.actualSize.tooltip"), button -> slider.update((int) minecraft.getMainWindow().getGuiScaleFactor() * 16)), enabled);
        useId = addButton(new HoverableCheckboxWidget(this, width / 2 - 100, height / 6 + 144, 98, 20, new TranslationTextComponent("block_renderer.gui.useId"), new TranslationTextComponent("block_renderer.gui.useId.tooltip"), false), enabled);
        addSize = addButton(new HoverableCheckboxWidget(this, width / 2 + 2, height / 6 + 144, 98, 20, new TranslationTextComponent("block_renderer.gui.addSize"), new TranslationTextComponent("block_renderer.gui.addSize.tooltip"), false), enabled);
    }
}
