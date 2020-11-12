package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.unascribed.blockrenderer.fabric.client.screens.BaseScreen;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableCheckboxWidget;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTinyButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

/*
 * Note: Screen's get initialized in init
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public abstract class BaseItemScreen extends BaseScreen {

    protected ButtonWidget actualSize;
    protected CheckboxWidget useId;
    protected CheckboxWidget addSize;

    public BaseItemScreen(Text title, @Nullable Screen old) {
        super(title, old);
    }

    @Override
    public void init() {
        assert client != null;
        boolean enabled = enabled();

        super.init();

        actualSize = addButton(new HoverableTinyButtonWidget(this, width / 2 + 104, height / 6 + 80, new TranslatableText("block_renderer.gui.actualSize"), new TranslatableText("block_renderer.gui.actualSize.tooltip"), button -> slider.update((int) client.getWindow().getScaleFactor() * 16)), enabled);
        useId = addButton(new HoverableCheckboxWidget(this, width / 2 - 100, height / 6 + 144, 98, 20, new TranslatableText("block_renderer.gui.useId"), new TranslatableText("block_renderer.gui.useId.tooltip"), false), enabled);
        addSize = addButton(new HoverableCheckboxWidget(this, width / 2 + 2, height / 6 + 144, 98, 20, new TranslatableText("block_renderer.gui.addSize"), new TranslatableText("block_renderer.gui.addSize.tooltip"), false), enabled);
    }
}
