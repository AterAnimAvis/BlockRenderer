package com.unascribed.blockrenderer.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.lib.TileRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static com.unascribed.blockrenderer.Reference.NAME;

/**
 * Static versions of AbstractGui and Screen utility methods.
 */
public interface Rendering {
	class DummyScreen extends Screen {

		protected DummyScreen() {
			super(new StringTextComponent(NAME + " Dummy Screen"));
		}

		@Override
		public List<String> getTooltipFromItem(ItemStack stack) {
			return super.getTooltipFromItem(stack);
		}
	}

	DummyScreen GUI = new DummyScreen();

	
	static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		GUI.drawCenteredString(fontRendererIn, text, x, y, color);
	}
	
	static void drawRect(int left, int top, int right, int bottom, int color) {
		AbstractGui.fill(left, top, right, bottom, color);
	}
	
	static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
		GUI.renderTooltip(textLines, x, y, font);
	}

	static void drawHoveringText(Screen owner, List<String> textLines, int x, int y) {
		GUI.init(Minecraft.getInstance(), owner.width, owner.height);
		GUI.renderTooltip(textLines, x, y);
	}
	
	static void drawBackground(int width, int height) {
		GUI.init(Minecraft.getInstance(), width, height);
		GUI.renderDirtBackground(0);
	}

	static void setupOverlayRendering() {
		Minecraft client = Minecraft.getInstance();
		MainWindow window = client.getMainWindow();
		double scaleFactor = window.getGuiScaleFactor();

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.loadIdentity();
		RenderSystem.ortho(0.0D, window.getFramebufferWidth() / scaleFactor, window.getFramebufferHeight() / scaleFactor, 0.0D, 1000.0D, 3000.0D);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.loadIdentity();
		RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
	}

	static void setupOverlayRendering(TileRenderer renderer) {
		Minecraft client = Minecraft.getInstance();
		MainWindow window = client.getMainWindow();
		double scaleFactor = window.getGuiScaleFactor();

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.loadIdentity();
		renderer.ortho(0.0D, renderer.imageWidth / scaleFactor, renderer.imageHeight / scaleFactor, 0.0D, 1000.0D, 3000.0D);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.loadIdentity();
		RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
	}


}
