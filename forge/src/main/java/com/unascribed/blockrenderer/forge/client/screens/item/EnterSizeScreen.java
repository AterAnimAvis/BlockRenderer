package com.unascribed.blockrenderer.forge.client.screens.item;

import com.unascribed.blockrenderer.forge.client.render.Requests;
import com.unascribed.blockrenderer.forge.client.render.manager.RenderManager;
import com.unascribed.blockrenderer.forge.client.screens.widgets.HoverableTinyButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.forge.client.varia.Strings.translate;

public class EnterSizeScreen extends BaseItemScreen {

    private static final TranslationTextComponent TITLE = translate("block_renderer.gui.renderItem");

    protected final ItemStack stack;
    private final boolean enableSwitch;

    public EnterSizeScreen(@Nullable Screen old, ItemStack stack) {
        this(TITLE, old, stack, true);
    }

    public EnterSizeScreen(ITextComponent title, @Nullable Screen old, ItemStack stack, boolean enableSwitch) {
        super(title, old);
        this.stack = stack;
        this.enableSwitch = enableSwitch;
    }

    @Override
    public void init() {
        assert minecraft != null;

        super.init();

        slider.y = height / 6 + 50;
        actualSize.y = height / 6 + 50;
        wikiSize.y = height / 6 + 50;

        addButton(new HoverableTinyButtonWidget(
                        this,
                        width - 32,
                        height - 32,
                        translate("block_renderer.gui.switch.bulk"),
                        translate("block_renderer.gui.switch.bulk.tooltip"),
                        button -> minecraft.displayGuiScreen(new EnterNamespaceScreen(old, stack))),
                enableSwitch
        );
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.displayGuiScreen(old);
        if (minecraft.world == null) return;

        RenderManager.push(Requests.single(stack, round(size), useId.isChecked(), addSize.isChecked()));
    }
}
