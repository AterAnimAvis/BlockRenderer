package com.unascribed.blockrenderer.fabric.client.proxy;

import com.mojang.blaze3d.platform.InputConstants;
import com.unascribed.blockrenderer.fabric.client.init.Keybindings;
import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.Requests;
import com.unascribed.blockrenderer.fabric.client.screens.SelectionScreen;
import com.unascribed.blockrenderer.fabric.client.screens.item.EnterSizeScreen;
import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import com.unascribed.blockrenderer.fabric.mixin.accessor.IHoveredSlot;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static com.unascribed.blockrenderer.fabric.client.varia.StringUtils.translate;

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

        Minecraft client = Minecraft.getInstance();
        Slot hovered = null;
        Screen currentScreen = client.screen;
        boolean isContainerScreen = currentScreen instanceof IHoveredSlot;

        // Intellij Bork-ing wants explicit null check.
        if (currentScreen != null && isContainerScreen) hovered = ((IHoveredSlot) currentScreen).block_renderer$accessor$hoveredSlot();

        if (Screen.hasControlDown()) {
            Player player = client.player;

            ItemStack input = hovered != null && hovered.hasItem() ? hovered.getItem() : null;
            if (input == null && player != null) input = player.getMainHandItem();

            client.setScreen(new SelectionScreen(client.screen, input));
            return;
        }

        if (!isContainerScreen) {
            Player player = client.player;

            if (player != null && !player.getMainHandItem().isEmpty()) {
                renderStack(player.getMainHandItem());
                return;
            }
            StringUtils.addMessage(translate("msg.block_renderer.notContainer"));
            return;
        }

        if (hovered == null) {
            StringUtils.addMessage(translate("msg.block_renderer.slot.absent"));
            return;
        }

        ItemStack stack = hovered.getItem();

        if (stack.isEmpty()) {
            StringUtils.addMessage(translate("msg.block_renderer.slot.empty"));
            return;
        }

        renderStack(stack);
    }

    private static void renderStack(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();

        if (Screen.hasShiftDown()) {
            client.setScreen(new EnterSizeScreen(client.screen, stack));
            return;
        }

        RenderManager.push(Requests.single(stack, 512, false, false));
    }

    private static boolean isKeyDown() {
        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.screen;

        /* Unbound key */
        if (Keybindings.render.isUnbound()) return false;

        /* Has the Keybinding been triggered? */
        if (Keybindings.render.isDown()) return true;

        /* Not in Screen so we should be ok */
        if (currentScreen == null) return false;

        /* Non Containers seem to behave ok */
        boolean hasSlots = currentScreen instanceof AbstractContainerScreen<?>;
        if (!hasSlots) return false;

        /* TextFieldWidgets */
        if (currentScreen.getFocused() instanceof EditBox) return false;

        /* Recipe Books */
        if (currentScreen instanceof RecipeUpdateListener) {
            RecipeBookComponent recipeBook = ((RecipeUpdateListener) currentScreen).getRecipeBookComponent();
            if (recipeBook.isVisible()) return false;
        }

        /* Actually Check to see if the key is down */
        InputConstants.Key key = KeyBindingHelper.getBoundKeyOf(Keybindings.render);

        if (key.getType() == InputConstants.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(client.getWindow().getWindow(), key.getValue()) == GLFW.GLFW_PRESS;
        }

        return InputConstants.isKeyDown(client.getWindow().getWindow(), key.getValue());
    }

}
