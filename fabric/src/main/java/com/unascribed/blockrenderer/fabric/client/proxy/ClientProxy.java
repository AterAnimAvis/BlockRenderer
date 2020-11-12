package com.unascribed.blockrenderer.fabric.client.proxy;

import com.unascribed.blockrenderer.fabric.client.init.Keybindings;
import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.screens.SelectionScreen;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import com.unascribed.blockrenderer.fabric.mixin.accessor.IHoveredSlot;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class ClientProxy {

    private static boolean down = false;

    public static void onFrameStart() {
        RenderManager.onFrameStart();

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

        // Intellij Bork-ing wants explicit null check.
        if (currentScreen != null && isContainerScreen) hovered = ((IHoveredSlot) currentScreen).block_renderer$accessor$hoveredSlot();

        if (Screen.hasControlDown()) {
            PlayerEntity player = client.player;

            ItemStack input = hovered != null && hovered.hasStack() ? hovered.getStack() : null;
            if (input == null && player != null) input = player.getMainHandStack();

            client.openScreen(new SelectionScreen(client.currentScreen, input));
            return;
        }

        if (!isContainerScreen) {
            PlayerEntity player = client.player;

            if (player != null && !player.getMainHandStack().isEmpty()) {
                renderStack(player.getMainHandStack());
                return;
            }
            StringUtils.addMessage(new TranslatableText("msg.block_renderer.notContainer"));
            return;
        }

        if (hovered == null) {
            StringUtils.addMessage(new TranslatableText("msg.block_renderer.slot.absent"));
            return;
        }

        ItemStack stack = hovered.getStack();

        if (stack.isEmpty()) {
            StringUtils.addMessage(new TranslatableText("msg.block_renderer.slot.empty"));
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

        RenderManager.push(ItemRenderer.single(stack, 512, false, false));
    }

    private static boolean isKeyDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen currentScreen = client.currentScreen;

        /* Unbound key */
        if (Keybindings.render.isUnbound()) return false;

        /* Has the Keybinding been triggered? */
        if (Keybindings.render.isPressed()) return true;

        /* Not in Screen so we should be ok */
        if (currentScreen == null) return false;

        /* Non Containers seem to behave ok */
        boolean hasSlots = currentScreen instanceof HandledScreen<?>;
        if (!hasSlots) return false;

        /* TextFieldWidgets */
        if (currentScreen.getFocused() instanceof TextFieldWidget) return false;

        /* Recipe Books */
        if (currentScreen instanceof RecipeBookProvider) {
            RecipeBookWidget recipeBook = ((RecipeBookProvider) currentScreen).getRecipeBookWidget();
            if (recipeBook.isOpen()) return false;
        }

        /* Actually Check to see if the key is down */
        InputUtil.Key key = KeyBindingHelper.getBoundKeyOf(Keybindings.render);

        if (key.getCategory() == InputUtil.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(client.getWindow().getHandle(), key.getCode()) == GLFW.GLFW_PRESS;
        }

        return InputUtil.isKeyPressed(client.getWindow().getHandle(), key.getCode());
    }

}
