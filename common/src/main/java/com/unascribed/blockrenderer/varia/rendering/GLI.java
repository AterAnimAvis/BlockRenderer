package com.unascribed.blockrenderer.varia.rendering;

import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import org.intellij.lang.annotations.MagicConstant;

public interface GLI {

    void pushMatrix();

    default void pushMatrix(String debug) {
        pushMatrix();
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "> Push Matrix {}", debug);
    }

    void popMatrix();

    default void popMatrix(String debug) {
        popMatrix();
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "< Pop Matrix {}", debug);
    }

    void loadIdentity();

    void ortho(double left, double right, double bottom, double top, double zNear, double zFar);

    /* ========================================================================================= Transformations ==== */

    void translate(float x, float y, float z);

    void scale(float x, float y, float z);

    default void scaleFixedZLevel(float scale, float zLevel) {
        translate(0F, 0F, scale * zLevel);
        scale(scale, scale, scale);
    }

    /* ================================================================================================ Lighting ==== */

    void setupItemStackLighting();

    void displayLighting();

    /* =================================================================================================== State ==== */

    void color(float r, float g, float b, float a);

    void enableDefaultBlend();

    void blendFunction(
            @MagicConstant(valuesFromClass = SourceFactor.class) int source,
            @MagicConstant(valuesFromClass = DestFactor.class) int dest
    );

    /* ================================================================================================== Buffer ==== */

    default void clearFrameBuffer() {
        resetClearColor();
        clearColorBuffer();
        clearDepthBuffer();
    }

    void resetClearColor();

    void clearColorBuffer();

    void clearDepthBuffer();

    /* ================================================================================================== Window ==== */

    void unbindFBO();

    void flipFrame();

    void rebindFBO();

    int getScaledWidth();

    int getScaledHeight();

    int getFramebufferWidth();

    int getFramebufferHeight();

    double getScaleFactor();

    /* ============================================================================================= Projections ==== */

    void matrixModeProjection();

    void matrixModeModelView();

    default void setupItemStackRendering(TileRenderer tr) {
        clearDepthBuffer();

        /* Projection */
        matrixModeProjection();
        loadIdentity();

        /* Setup Orthographic Projection */
        /*
            Whilst we can switch the top/bottom parameters to save flipping the image later,
            culling issues sometime occur.
        */
        tr.orthographic(0.0, tr.getImageWidth(), tr.getImageHeight(), 0, 1000.0, 3000.0);

        /* Model View */
        matrixModeModelView();
        loadIdentity();
        translate(0f, 0f, -2000f);
    }

    default void setupMapRendering(TileRenderer tr) {
        clearDepthBuffer();

        /* Projection */
        matrixModeProjection();
        loadIdentity();

        //TODO: Reduce Near / Far
        tr.orthographic(0.0, tr.getImageWidth(), tr.getImageHeight(), 0, -1000.0F, 3000.0f);

        /* Model View */
        matrixModeModelView();
        loadIdentity();
    }

    default void setupOverlayRendering() {
        double scaleFactor = getScaleFactor();

        clearDepthBuffer();
        matrixModeProjection();
        loadIdentity();
        ortho(0.0D, getFramebufferWidth() / scaleFactor, getFramebufferHeight() / scaleFactor, 0.0D, 1000.0D, 3000.0D);
        matrixModeModelView();
        loadIdentity();
        translate(0.0F, 0.0F, -2000.0F);
    }

    /* =============================================================================================== Constants ==== */

    interface SourceFactor {
        int CONSTANT_ALPHA = 32771;
        int CONSTANT_COLOR = 32769;
        int DST_ALPHA = 772;
        int DST_COLOR = 774;
        int ONE = 1;
        int ONE_MINUS_CONSTANT_ALPHA = 32772;
        int ONE_MINUS_CONSTANT_COLOR = 32770;
        int ONE_MINUS_DST_ALPHA = 773;
        int ONE_MINUS_DST_COLOR = 775;
        int ONE_MINUS_SRC_ALPHA = 771;
        int ONE_MINUS_SRC_COLOR = 769;
        int SRC_ALPHA = 770;
        int SRC_ALPHA_SATURATE = 776;
        int SRC_COLOR = 768;
        int ZERO = 0;
    }

    interface DestFactor {
        int CONSTANT_ALPHA = 32771;
        int CONSTANT_COLOR = 32769;
        int DST_ALPHA = 772;
        int DST_COLOR = 774;
        int ONE = 1;
        int ONE_MINUS_CONSTANT_ALPHA = 32772;
        int ONE_MINUS_CONSTANT_COLOR = 32770;
        int ONE_MINUS_DST_ALPHA = 773;
        int ONE_MINUS_DST_COLOR = 775;
        int ONE_MINUS_SRC_ALPHA = 771;
        int ONE_MINUS_SRC_COLOR = 769;
        int SRC_ALPHA = 770;
        int SRC_COLOR = 768;
        int ZERO = 0;
    }

}
