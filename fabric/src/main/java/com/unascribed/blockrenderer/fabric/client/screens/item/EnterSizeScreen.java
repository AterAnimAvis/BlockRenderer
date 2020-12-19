package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.Requests;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTinyButtonWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.fabric.client.varia.Strings.translate;

public class EnterSizeScreen extends BaseItemScreen {

    private static final TranslatableComponent TITLE = translate("block_renderer.gui.renderItem");

    protected final ItemStack stack;
    private final boolean enableSwitch;

    public EnterSizeScreen(@Nullable Screen old, ItemStack stack) {
        this(TITLE, old, stack, true);
    }

    public EnterSizeScreen(Component title, @Nullable Screen old, ItemStack stack, boolean enableSwitch) {
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
        actualSize.visible = enableSwitch;

        addButton(new HoverableTinyButtonWidget(
                        this,
                        width - 32,
                        height - 32,
                        translate("block_renderer.gui.switch.bulk"),
                        translate("block_renderer.gui.switch.bulk.tooltip"),
                        button -> minecraft.setScreen(new EnterNamespaceScreen(old, stack))),
                enableSwitch
        );
    }

    @Override
    public void onRender(Button button) {
        assert minecraft != null;

        minecraft.setScreen(old);
        if (minecraft.level == null) return;

        RenderManager.push(Requests.single(stack, round(size), useId.selected(), addSize.selected()));
    }
}
