package com.unascribed.blockrenderer.fabric.client.render.report;

import com.unascribed.blockrenderer.fabric.client.varia.Strings;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import com.unascribed.blockrenderer.render.report.BaseReporterBulk;
import net.minecraft.util.text.ITextComponent;

public class ReporterBulk extends BaseReporterBulk<ITextComponent> {

    public ReporterBulk(String name, int total) {
        super(Display.INSTANCE, GL.INSTANCE, name, Strings.translate("block_renderer.gui.rendering", total, name), total);
    }

    @Override
    protected ITextComponent getCancelled() {
        return Strings.translate("block_renderer.gui.renderCancelled");
    }

    @Override
    protected ITextComponent getProgress(int rendered, int total, int remaining) {
        return Strings.translate("block_renderer.gui.progress", rendered, total, remaining);
    }

    @Override
    protected ITextComponent getFinished(String name, int total) {
        return Strings.translate("block_renderer.gui.rendered", total, name);
    }
}
