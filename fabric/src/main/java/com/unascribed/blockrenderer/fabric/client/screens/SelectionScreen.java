package com.unascribed.blockrenderer.fabric.client.screens;


import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterNamespaceScreen;
import com.unascribed.blockrenderer.fabric.client.screens.item.RenderAnimatedScreen;
import com.unascribed.blockrenderer.fabric.client.screens.map.RenderMapScreen;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.ItemButtonWidget;
import com.unascribed.blockrenderer.fabric.client.varia.Identifiers;
import com.unascribed.blockrenderer.fabric.client.varia.Registries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectionScreen extends Screen {

    private static final TranslatableComponent TITLE = new TranslatableComponent("block_renderer.gui.choose");

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
                Registries.mapLazy(Registries.MAP, Item::getDefaultInstance),
                width / 2 - 64 - 12,
                height / 2,
                new TranslatableComponent("block_renderer.gui.choose.map"),
                new TranslatableComponent("block_renderer.gui.choose.map.tooltip"),
                button -> {
                    assert stack != null;
                    minecraft.setScreen(new RenderMapScreen(old, stack));
                }
        )).active = stack != null && Objects.equals(Identifiers.get(stack.getItem()), new ResourceLocation("minecraft:filled_map"));

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.mapLazy(Registries.DISPENSER, Item::getDefaultInstance),
                width / 2,
                height / 2,
                new TranslatableComponent("block_renderer.gui.choose.item"),
                new TranslatableComponent("block_renderer.gui.choose.item.tooltip"),
                button -> minecraft.setScreen(new EnterNamespaceScreen(old, stack))
        ));

        addButton(new ItemButtonWidget(
                this,
                itemRenderer,
                Registries.mapLazy(Registries.CUTTER, Item::getDefaultInstance),
                width / 2 + 64 + 12,
                height / 2,
                new TranslatableComponent("block_renderer.gui.choose.animated"),
                new TranslatableComponent("block_renderer.gui.choose.animated.tooltip"),
                button -> {
                    assert stack != null;
                    minecraft.setScreen(new RenderAnimatedScreen(old, stack));
                }
        )).active = stack != null;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        renderBackground(stack);

        super.render(stack, mouseX, mouseY, partialTicks);

        drawCenteredString(stack, minecraft.font, title, width / 2, height / 6, -1);
    }

    @Override
    public void onClose() {
        assert minecraft != null;

        minecraft.setScreen(old);
    }

}
