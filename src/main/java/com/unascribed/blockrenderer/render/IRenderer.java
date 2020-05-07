package com.unascribed.blockrenderer.render;

import com.google.common.io.Files;
import com.unascribed.blockrenderer.lib.TileRenderer;
import com.unascribed.blockrenderer.utils.ImageUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.unascribed.blockrenderer.utils.FileUtils.getFile;
import static com.unascribed.blockrenderer.utils.StringUtils.getRenderSuccess;
import static com.unascribed.blockrenderer.utils.StringUtils.sanitize;

public interface IRenderer<T> {

    File DEFAULT_FOLDER = new File("renders");

    void setup(int size);

    void render(T value);

    void teardown();

    void renderTooltip(MatrixStack stack, T value, int displayWidth, int displayHeight);

    Identifier getId(T value);

    Text getName(T value);

    default String getFilename(T value, boolean useId) {
        return useId ? sanitize(getId(value)) : sanitize(getName(value));
    }

    @Nullable
    TileRenderer getRenderer();

    @SuppressWarnings("UnstableApiUsage")
    default File saveRaw(File folder, String filename) throws Exception {
        TileRenderer renderer = getRenderer();

        if (renderer == null) throw new IllegalStateException("TileRenderer is null");

        BufferedImage img = ImageUtils.readPixels(renderer);

        File file = getFile(folder, filename);
        Files.createParentDirs(file);

        ImageIO.write(img, "PNG", file);

        return file;
    }

    default Text save(T value, File folder, String filename) {
        try {
            File file = saveRaw(folder, filename);
            return getRenderSuccess(folder, file);
        } catch (Exception e) {
            System.err.println("Rendering: " + getId(value));
            e.printStackTrace();
            return new TranslatableText("msg.block_renderer.render.fail");
        }
    }

}
