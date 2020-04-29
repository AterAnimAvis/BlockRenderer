package com.unascribed.blockrenderer.screens;

import com.unascribed.blockrenderer.BlockRenderer;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class EnterNamespaceScreen extends Screen {

	private static final int MIN_SIZE = 16;
	private static final int MAX_SIZE = 2048;

	private static final TranslationTextComponent TITLE = new TranslationTextComponent("blockrenderer.gui.namespace");

	private final String prefill;
	private final Screen old;

	private TextFieldWidget text;
	private double size = 512;
	
	public EnterNamespaceScreen(@Nullable Screen old, String prefill) {
		super(TITLE);
		this.old = old;
		this.prefill = prefill;
	}
	
	@Override
	public void init() {
		assert minecraft != null;
		boolean enabled = minecraft.world != null;

		String oldText = (text == null ? prefill : text.getText());

		text = addButton(new TextFieldWidget(minecraft.fontRenderer, width/2-100, height/6+50, 200, 20, I18n.format("blockrenderer.gui.namespace")), enabled);
		text.setText(oldText);
		text.setFocused2(true);
		text.setCanLoseFocus(false);
		text.setEnabled(enabled);

		setFocusedDefault(text);
		
		addButton(new Button(width/2-100, height/6+120, 98, 20, I18n.format("gui.cancel"), button -> minecraft.displayGuiScreen(old)));

		addButton(new Button(width/2+2, height/6+120, 98, 20, I18n.format("blockrenderer.gui.render"), button -> {
			minecraft.displayGuiScreen(old);
			if (minecraft.world == null) return;

			BlockRenderer.pendingBulkRender = text.getText();
			BlockRenderer.pendingBulkRenderSize = round(size);
		}), enabled);

		int displayWidth = minecraft.getMainWindow().getFramebufferWidth();
		int displayHeight = minecraft.getMainWindow().getFramebufferHeight();

		int maxSize = Math.min(Math.min(displayWidth, displayHeight), MAX_SIZE);
		size = MathHelper.clamp(size, MIN_SIZE, maxSize);

		SliderPercentageOption option = new SliderPercentageOption(I18n.format("blockrenderer.gui.renderSize"), MIN_SIZE, maxSize, 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
		addButton(new OptionSlider(minecraft.gameSettings, width/2-100, height/6+80, 200, 20, option), enabled);
	}

	private int round(double value) {
		assert minecraft != null;

		int val = (int)value;

		int displayWidth = minecraft.getMainWindow().getFramebufferWidth();
		int displayHeight = minecraft.getMainWindow().getFramebufferHeight();

		// There's a more efficient method in MathHelper, but it rounds up. We want the nearest.
		int nearestPowerOfTwo = (int)Math.pow(2, Math.ceil(Math.log(val)/Math.log(2)));
		int maxSize = Math.min(Math.min(displayHeight, displayWidth), MAX_SIZE);

		if (nearestPowerOfTwo < maxSize && Math.abs(val-nearestPowerOfTwo) < 32) val = nearestPowerOfTwo;

		return MathHelper.clamp(val, MIN_SIZE, maxSize);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		assert minecraft != null;

		renderBackground();

		super.render(mouseX, mouseY, partialTicks);

		drawCenteredString(minecraft.fontRenderer, I18n.format("blockrenderer.gui.namespace"), width/2, height/6, -1);

		if (minecraft.world == null) {
			drawCenteredString(minecraft.fontRenderer, I18n.format("blockrenderer.gui.noWorld"), width/2, height/6+30, 0xFF5555);
			return;
		}

		int displayWidth = minecraft.getMainWindow().getFramebufferWidth();
		int displayHeight = minecraft.getMainWindow().getFramebufferHeight();

		boolean widthCap = (displayWidth < 2048);
		boolean heightCap = (displayHeight < 2048);

		String str = null;

		if (widthCap && heightCap) {
			if (displayWidth > displayHeight) {
				str = "blockrenderer.gui.cappedHeight";
			} else if (displayWidth == displayHeight) {
				str = "blockrenderer.gui.cappedBoth";
			} else {
				str = "blockrenderer.gui.cappedWidth";
			}
		} else if (widthCap) {
			str = "blockrenderer.gui.cappedWidth";
		} else if (heightCap) {
			str = "blockrenderer.gui.cappedHeight";
		}

		if (str == null) return;

		drawCenteredString(minecraft.fontRenderer, I18n.format(str, Math.min(displayHeight, displayWidth)), width/2, height/6+104, 0xFFFFFF);
	}

	@Override
	public void tick() {
		super.tick();
		text.tick();
	}

	public String getSliderDisplay(GameSettings settings, SliderPercentageOption option) {
		int px = round(size);
		return option.getDisplayString() + px + "x" + px;
	}

	private <T extends Widget> T addButton(T button, boolean active) {
		addButton(button);

		button.active = active;
		button.visible = active;

		return button;
	}

}
