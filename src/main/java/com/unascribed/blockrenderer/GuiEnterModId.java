package com.unascribed.blockrenderer;

import com.google.common.base.Strings;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiEnterModId extends Screen {

	private static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.entermodid");

	private String prefill;
	private TextFieldWidget text;
	private double size = 512;
	private Screen old;
	
	public GuiEnterModId(Screen old, String prefill) {
		super(TITLE);
		this.old = old;
		this.prefill = Strings.nullToEmpty(prefill);
	}
	
	@Override
	public void init() {
		assert minecraft != null;
		boolean enabled = minecraft.world != null;

		String oldText = (text == null ? prefill : text.getText());

		text = addButton(new TextFieldWidget(minecraft.fontRenderer, width/2-100, height/6+50, 200, 20, I18n.format("gui.entermodid")));
		text.setText(oldText);
		text.setFocused2(true);
		text.setCanLoseFocus(false);
		text.setEnabled(enabled);

		setFocusedDefault(text);
		
		addButton(new Button(width/2-100, height/6+120, 98, 20, I18n.format("gui.cancel"), button -> minecraft.displayGuiScreen(old)));

		Button render = addButton(new Button(width/2+2, height/6+120, 98, 20, I18n.format("gui.render"), button -> {
			minecraft.displayGuiScreen(old);
			if (minecraft.world == null) return;

			BlockRenderer.inst.pendingBulkRender = text.getText();
			BlockRenderer.inst.pendingBulkRenderSize = round(size);
		}));

		int displayWidth = minecraft.getMainWindow().getFramebufferWidth();
		int displayHeight = minecraft.getMainWindow().getFramebufferHeight();

		int minSize = Math.min(displayWidth, displayHeight);

		SliderPercentageOption option = new SliderPercentageOption(I18n.format("gui.rendersize"), 16, Math.min(2048, minSize), 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
		OptionSlider slider = addButton(new OptionSlider(minecraft.gameSettings, width/2-100, height/6+80, 200, 20, option));

		text.active = enabled;
		text.visible = enabled;
		render.active = enabled;
		render.visible = enabled;
		slider.active = enabled;
		slider.visible = enabled;
	}

	private int round(double value) {
		assert minecraft != null;

		int val = (int)value;

		int displayWidth = minecraft.getMainWindow().getFramebufferWidth();
		int displayHeight = minecraft.getMainWindow().getFramebufferHeight();
		// There's a more efficient method in MathHelper, but it rounds up. We want the nearest.
		int nearestPowerOfTwo = (int)Math.pow(2, Math.ceil(Math.log(val)/Math.log(2)));
		int minSize = Math.min(displayHeight, displayWidth);
		if (nearestPowerOfTwo < minSize && Math.abs(val-nearestPowerOfTwo) < 32) {
			val = nearestPowerOfTwo;
		}
		return Math.min(val, minSize);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		assert minecraft != null;

		renderBackground();

		super.render(mouseX, mouseY, partialTicks);

		drawCenteredString(minecraft.fontRenderer, I18n.format("gui.entermodid"), width/2, height/6, -1);
		if (minecraft.world == null) {
			drawCenteredString(minecraft.fontRenderer, I18n.format("gui.noworld"), width/2, height/6+30, 0xFF5555);
		} else {
			int displayWidth = minecraft.getMainWindow().getFramebufferWidth();
			int displayHeight = minecraft.getMainWindow().getFramebufferHeight();

			boolean widthCap = (displayWidth < 2048);
			boolean heightCap = (displayHeight < 2048);
			String str = null;
			if (widthCap && heightCap) {
				if (displayWidth > displayHeight) {
					str = "gui.cappedheight";
				} else if (displayWidth == displayHeight) {
					str = "gui.cappedboth";
				} else {
					str = "gui.cappedwidth";
				}
			} else if (widthCap) {
				str = "gui.cappedwidth";
			} else if (heightCap) {
				str = "gui.cappedheight";
			}
			if (str != null) {
				drawCenteredString(minecraft.fontRenderer, I18n.format(str, Math.min(displayHeight, displayWidth)), width/2, height/6+104, 0xFFFFFF);
			}
		}
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
}
