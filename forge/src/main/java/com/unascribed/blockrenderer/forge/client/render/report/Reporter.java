package com.unascribed.blockrenderer.forge.client.render.report;

import com.unascribed.blockrenderer.forge.client.varia.rendering.Display;
import com.unascribed.blockrenderer.render.report.BaseReporter;
import net.minecraft.util.text.ITextComponent;

import static com.unascribed.blockrenderer.forge.client.varia.StringUtils.rawText;

public class Reporter extends BaseReporter<ITextComponent> {

    public static Reporter INSTANCE = new Reporter();

    private Reporter() {
        super(rawText("Rendering"), Display.INSTANCE);
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
