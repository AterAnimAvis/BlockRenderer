package com.unascribed.blockrenderer.fabric.client.render.report;

import com.unascribed.blockrenderer.InternalAPI;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.render.report.BaseReporter;
import net.minecraft.util.text.ITextComponent;

import static com.unascribed.blockrenderer.fabric.client.varia.StringUtils.rawText;

public class Reporter extends BaseReporter<ITextComponent> {

    public static final Reporter INSTANCE = new Reporter();

    private Reporter() {
        super(Display.INSTANCE, InternalAPI.getGL(), rawText("Rendering"));
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
    public ITextComponent getProgress() {
        return rawText(getProgressString());
    }

}
