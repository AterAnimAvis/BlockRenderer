package com.unascribed.blockrenderer.fabric.client.render.report;

import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.render.report.BaseReporter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class Reporter extends BaseReporter<Component> {

    public static Reporter INSTANCE = new Reporter();

    private Reporter() {
        super(new TextComponent("Rendering"), Display.INSTANCE);
    }

    @Override
    public void pop() {
        pop(title.getString(), message == null ? "null" : message.getString());
    }

    @Override
    public void end() {
        end(title.getString(), message == null ? "null" : message.getString());
    }

    @Override
    public Component getProgress() {
        return new TextComponent(getProgressString());
    }

}
