package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTinyButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class EnterSizeScreen extends BaseItemScreen {

    private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.renderItem");

    protected final ItemStack stack;
    private final boolean enableSwitch;

    public EnterSizeScreen(@Nullable Screen old, ItemStack stack) {
        this(TITLE, old, stack, true);
    }

    public EnterSizeScreen(Text title, @Nullable Screen old, ItemStack stack, boolean enableSwitch) {
        super(title, old);
        this.stack = stack;
        this.enableSwitch = enableSwitch;
    }

    @Override
    public void init() {
        assert client != null;

        super.init();

        slider.y = height / 6 + 50;
        actualSize.y = height / 6 + 50;
        actualSize.visible = enableSwitch;
        wikiSize.y = height / 6 + 50;
        wikiSize.visible = enableSwitch;

        addButton(new HoverableTinyButtonWidget(
                        this,
                        width - 32,
                        height - 32,
                        new TranslatableText("block_renderer.gui.switch.bulk"),
                        new TranslatableText("block_renderer.gui.switch.bulk.tooltip"),
                        button -> client.openScreen(new EnterNamespaceScreen(old, stack))),
                enableSwitch
        );
    }

    @Override
    public void onRender(ButtonWidget button) {
        assert client != null;

        client.openScreen(old);
        if (client.world == null) return;

        RenderManager.push(ItemRenderer.single(stack, round(size), useId.isChecked(), addSize.isChecked()));
    }
}
