package com.unascribed.blockrenderer.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.client.screens.item.EnterNamespaceScreen;
import com.unascribed.blockrenderer.client.screens.item.RenderAnimatedScreen;
import com.unascribed.blockrenderer.client.screens.map.RenderMapScreen;
import com.unascribed.blockrenderer.client.screens.widgets.ItemButtonWidget;
import com.unascribed.blockrenderer.client.varia.Registries;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectionScreen extends Screen {

    private static final TranslationTextComponent TITLE = new TranslationTextComponent("block_renderer.gui.choose");

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
        assert minecraft != null;

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.MAP.lazyMap(Item::getDefaultInstance),
                width / 2 - 64 - 12,
                height / 2,
                new TranslationTextComponent("block_renderer.gui.choose.map"),
                new TranslationTextComponent("block_renderer.gui.choose.map.tooltip"),
                button -> {
                    assert stack != null;
                    minecraft.displayGuiScreen(new RenderMapScreen(old, stack));
                }
        )).active = stack != null && Objects.equals(stack.getItem().getRegistryName(), new ResourceLocation("minecraft:filled_map"));

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.DISPENSER.lazyMap(Item::getDefaultInstance),
                width / 2,
                height / 2,
                new TranslationTextComponent("block_renderer.gui.choose.item"),
                new TranslationTextComponent("block_renderer.gui.choose.item.tooltip"),
                button -> minecraft.displayGuiScreen(new EnterNamespaceScreen(old, stack))
        ));

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.CUTTER.lazyMap(Item::getDefaultInstance),
                width / 2 + 64 + 12,
                height / 2,
                new TranslationTextComponent("block_renderer.gui.choose.animated"),
                new TranslationTextComponent("block_renderer.gui.choose.animated.tooltip"),
                button -> {
                    assert stack != null;
                    minecraft.displayGuiScreen(new RenderAnimatedScreen(old, stack));
                }
        )).active = stack != null;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        renderBackground(stack);

        super.render(stack, mouseX, mouseY, partialTicks);

        drawCenteredString(stack, minecraft.fontRenderer, title, width / 2, height / 6, -1);
    }

    @Override
    public void closeScreen() {
        assert minecraft != null;

        minecraft.displayGuiScreen(old);
    }

}