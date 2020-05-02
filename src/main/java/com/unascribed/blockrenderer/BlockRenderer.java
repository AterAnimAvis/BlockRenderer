package com.unascribed.blockrenderer;

import com.unascribed.blockrenderer.init.Keybindings;
import com.unascribed.blockrenderer.mixin.accessor.IHoveredSlot;
import com.unascribed.blockrenderer.render.ItemStackRenderer;
import com.unascribed.blockrenderer.render.request.IRequest;
import com.unascribed.blockrenderer.screens.EnterNamespaceScreen;
import com.unascribed.blockrenderer.screens.EnterSizeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

import static com.unascribed.blockrenderer.utils.StringUtils.addMessage;

public class BlockRenderer implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("BlockRenderer");

	private static boolean down = false;

	@Nullable
	public static IRequest pendingRequest;

	@Override
	public void onInitializeClient() {
		Keybindings.register();
	}


	public static void onFrameStart() {
		/*
		 * Quick primer: OpenGL is double-buffered. This means, where we draw to is
		 * /not/ on the screen. As such, we are free to do whatever we like before
		 * Minecraft renders, as long as we put everything back the way it was.
		 */

		if (pendingRequest != null) {
			// We *must* call render code in pre-render. If we don't, it won't work right.
			pendingRequest.render();
			pendingRequest = null;
		}

		if (!isKeyDown()) {
			down = false;
			return;
		}

		if (down) return;
		down = true;

		MinecraftClient client = MinecraftClient.getInstance();
		Slot hovered = null;
		Screen currentScreen = client.currentScreen;
		boolean isContainerScreen = currentScreen instanceof IHoveredSlot;

		if (isContainerScreen) hovered = ((IHoveredSlot) currentScreen).getHoveredSlot();

		if (Screen.hasControlDown()) {
			String namespace = "";
			if (hovered != null && hovered.hasStack()) {
				Identifier identifier = Registry.ITEM.getId(hovered.getStack().getItem());
				if (identifier != Registry.ITEM.getDefaultId()) namespace = identifier.getNamespace();
			}

			PlayerEntity player = client.player;
			if (!isContainerScreen && player != null && !player.getMainHandStack().isEmpty()) {
				Identifier identifier = Registry.ITEM.getId(player.getMainHandStack().getItem());
				if (identifier != Registry.ITEM.getDefaultId()) namespace = identifier.getNamespace();
			}

			client.openScreen(new EnterNamespaceScreen(client.currentScreen, namespace.trim()));
			return;
		}

		if (!isContainerScreen) {
			PlayerEntity player = client.player;

			if (player != null && !player.getMainHandStack().isEmpty()) {
				renderStack(player.getMainHandStack());
				return;
			}
			addMessage(new TranslatableText("msg.blockrenderer.notContainer"));
			return;
		}

		if (hovered == null) {
			addMessage(new TranslatableText("msg.blockrenderer.slot.absent"));
			return;
		}

		ItemStack stack = hovered.getStack();

		if (stack.isEmpty()) {
			addMessage(new TranslatableText("msg.blockrenderer.slot.empty"));
			return;
		}

		renderStack(stack);
	}

	private static void renderStack(ItemStack stack) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (Screen.hasShiftDown()) {
			client.openScreen(new EnterSizeScreen(client.currentScreen, stack));
			return;
		}

		ItemStackRenderer.renderItem(512, stack, false, false);
	}

	private static boolean isKeyDown() {
		MinecraftClient client = MinecraftClient.getInstance();
		Screen currentScreen = client.currentScreen;

		/* Unbound key */
		if (Keybindings.render.isNotBound()) return false;

		/* Has the Keybinding been triggered? */
		if (Keybindings.render.isPressed()) return true;

		/* Not in Screen so we should be ok */
		if (currentScreen == null) return false;

		/* Non Containers seem to behave ok */
		boolean hasSlots = currentScreen instanceof IHoveredSlot;
		if (!hasSlots) return false;

		/* TextFieldWidgets */
		if (currentScreen.getFocused() instanceof TextFieldWidget) return false;

		/* Recipe Books */
		if (currentScreen instanceof RecipeBookProvider) {
			RecipeBookWidget recipeBook = ((RecipeBookProvider) currentScreen).getRecipeBookWidget();
			if (recipeBook.isOpen()) return false;
		}

		/* Actually Check to see if the key is down */
		InputUtil.KeyCode key = Keybindings.render.getBoundKey();

		if (key.getCategory() == InputUtil.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(client.getWindow().getHandle(), key.getKeyCode()) == GLFW.GLFW_PRESS;
		}

		return InputUtil.isKeyPressed(client.getWindow().getHandle(), key.getKeyCode());
	}


}
