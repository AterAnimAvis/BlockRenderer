package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.BlockRenderer;
import com.unascribed.blockrenderer.render.request.ItemRequest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class EnterSizeScreen extends BaseScreen {

	private static final TranslationTextComponent TITLE = new TranslationTextComponent("blockrenderer.gui.renderItem");

	private final ItemStack stack;

	public EnterSizeScreen(@Nullable Screen old, ItemStack stack) {
		super(TITLE, old);
		this.stack = stack;
	}

	@Override
	public void init() {
		super.init();
		slider.y = height / 6 + 50;
	}

	@Override
	void onRender(Button button) {
		assert minecraft != null;

		minecraft.displayGuiScreen(old);
		if (minecraft.world == null) return;

		BlockRenderer.pendingRequest = new ItemRequest(round(size), stack, useId.func_212942_a(), addSize.func_212942_a());
	}
}
