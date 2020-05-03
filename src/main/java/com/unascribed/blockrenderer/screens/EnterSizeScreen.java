package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.BlockRenderer;
import com.unascribed.blockrenderer.render.request.item.ItemRequest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;

public class EnterSizeScreen extends BaseScreen {

	private static final TranslatableText TITLE = new TranslatableText("blockrenderer.gui.renderItem");

	private final ItemStack stack;

	public EnterSizeScreen(@Nullable Screen old, ItemStack stack) {
		super(TITLE, old);
		this.stack = stack;
	}

	@Override
	public void init() {
		super.init();
		slider.y = height / 6 + 50;
		actualSize.y = height / 6 + 50;
	}

	@Override
	void onRender(AbstractButtonWidget button) {
		assert client != null;

		client.openScreen(old);
		if (client.world == null) return;

		BlockRenderer.pendingRequest = new ItemRequest(round(size), stack, useId.isChecked(), addSize.isChecked());
	}
}
