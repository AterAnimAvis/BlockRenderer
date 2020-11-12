package com.unascribed.blockrenderer.fabric.client.screens;


import com.unascribed.blockrenderer.fabric.client.screens.item.EnterNamespaceScreen;
import com.unascribed.blockrenderer.fabric.client.screens.item.RenderAnimatedScreen;
import com.unascribed.blockrenderer.fabric.client.screens.map.RenderMapScreen;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.ItemButtonWidget;
import com.unascribed.blockrenderer.fabric.client.varia.Identifiers;
import com.unascribed.blockrenderer.fabric.client.varia.Registries;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectionScreen extends Screen {

    private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.choose");

    @Nullable
    private final Screen old;

    @Nullable
    private final ItemStack stack;

    public SelectionScreen(@Nullable Screen old, @Nullable ItemStack stack) {
        super(TITLE);
        this.old = old;
        this.stack = stack;
    }

    @Override
    protected void init() {
        assert client != null;

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.mapLazy(Registries.MAP, Item::getDefaultStack),
                width / 2 - 64 - 12,
                height / 2,
                new TranslatableText("block_renderer.gui.choose.map"),
                new TranslatableText("block_renderer.gui.choose.map.tooltip"),
                button -> {
                    assert stack != null;
                    client.openScreen(new RenderMapScreen(old, stack));
                }
        )).active = stack != null && Objects.equals(Identifiers.get(stack.getItem()), new Identifier("minecraft:filled_map"));

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.mapLazy(Registries.DISPENSER, Item::getDefaultStack),
                width / 2,
                height / 2,
                new TranslatableText("block_renderer.gui.choose.item"),
                new TranslatableText("block_renderer.gui.choose.item.tooltip"),
                button -> client.openScreen(new EnterNamespaceScreen(old, stack))
        ));

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.mapLazy(Registries.CUTTER, Item::getDefaultStack),
                width / 2 + 64 + 12,
                height / 2,
                new TranslatableText("block_renderer.gui.choose.animated"),
                new TranslatableText("block_renderer.gui.choose.animated.tooltip"),
                button -> {
                    assert stack != null;
                    client.openScreen(new RenderAnimatedScreen(old, stack));
                }
        )).active = stack != null;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert client != null;

        renderBackground(stack);

        super.render(stack, mouseX, mouseY, partialTicks);

        drawCenteredText(stack, client.textRenderer, title, width / 2, height / 6, -1);
    }

    @Override
    public void onClose() {
        assert client != null;

        client.openScreen(old);
    }

}
