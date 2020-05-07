package com.unascribed.blockrenderer.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.lib.TileRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static com.unascribed.blockrenderer.Reference.NAME;

/**
 * Static versions of AbstractGui and Screen utility methods.
 */
public interface Rendering {
	class DummyScreen extends Screen {

		protected DummyScreen() {
			super(new LiteralText(NAME + " Dummy Screen"));
		}

		@Override
		public List<Text> getTooltipFromItem(ItemStack stack) {
			return super.getTooltipFromItem(stack);
		}
	}

	DummyScreen GUI = new DummyScreen();

	
	static void drawCenteredString(MatrixStack matrices, TextRenderer fontRendererIn, String text, int x, int y, int color) {
		GUI.drawCenteredString(matrices, fontRendererIn, text, x, y, color);
	}
	
	static void drawRect(MatrixStack matrices, int left, int top, int right, int bottom, int color) {
		DrawableHelper.fill(matrices, left, top, right, bottom, color);
	}
	
	static void drawHoveringText(MatrixStack matrices, List<Text> textLines, int x, int y) {
		GUI.renderTooltip(matrices, textLines, x, y);
	}

	static void drawHoveringText(Screen owner, MatrixStack matrices, List<Text> textLines, int x, int y) {
		GUI.init(MinecraftClient.getInstance(), owner.width, owner.height);
		GUI.renderTooltip(matrices, textLines, x, y);
	}
	
	static void drawBackground(int width, int height) {
		GUI.init(MinecraftClient.getInstance(), width, height);
		GUI.renderBackgroundTexture(0);
	}

	static void setupOverlayRendering() {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = client.getWindow();
		double scaleFactor = window.getScaleFactor();

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.loadIdentity();
		RenderSystem.ortho(0.0D, window.getFramebufferWidth() / scaleFactor, window.getFramebufferHeight() / scaleFactor, 0.0D, 1000.0D, 3000.0D);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.loadIdentity();
		RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
	}

	static void setupOverlayRendering(TileRenderer renderer) {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = client.getWindow();
		double scaleFactor = window.getScaleFactor();

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.loadIdentity();
		renderer.ortho(0.0D, renderer.imageWidth / scaleFactor, renderer.imageHeight / scaleFactor, 0.0D, 1000.0D, 3000.0D);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.loadIdentity();
		RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
	}


}
