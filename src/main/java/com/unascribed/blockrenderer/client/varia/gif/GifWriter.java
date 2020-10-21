package com.unascribed.blockrenderer.client.varia.gif;

import net.minecraft.util.math.MathHelper;
import org.w3c.dom.Node;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class GifWriter implements Closeable {

    protected ImageWriter writer;
    protected ImageWriteParam parameters;
    protected IIOMetadata metadata;

    public GifWriter(ImageOutputStream stream, int delayMS, boolean loop) throws IOException {
        writer = getGifWriter();
        parameters = writer.getDefaultWriteParam();

        ImageTypeSpecifier specifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
        metadata = writer.getDefaultImageMetadata(specifier, parameters);

        setupMetadata(metadata, delayMS, loop);

        writer.setOutput(stream);
        writer.prepareWriteSequence(null);
    }

    public void writeFrame(RenderedImage image) throws IOException {
        writer.writeToSequence(new IIOImage(image, null, metadata), parameters);
    }

    @Override
    public void close() throws IOException {
        writer.endWriteSequence();
    }

    private static ImageWriter getGifWriter() throws IOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");

        if (iter.hasNext()) return iter.next();

        throw new IOException("No Gif Writers were found");
    }

    private static void setupMetadata(IIOMetadata metadata, int delayMS, boolean loop) throws IOException {
        IIOMetadataNode node = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

        /* https://docs.oracle.com/javase/8/docs/api/javax/imageio/metadata/doc-files/gif_metadata.html */

        /* Setup Graphics */
        IIOMetadataNode graphics = findOrCreateNode(node, "GraphicControlExtension");
        graphics.setAttribute("disposalMethod", "restoreToBackgroundColor");
        graphics.setAttribute("userInputFlag", "FALSE");
        graphics.setAttribute("transparentColorFlag", "FALSE");
        graphics.setAttribute("transparentColorIndex", "0");

        /* Setup Delay */
        graphics.setAttribute("delayTime", String.valueOf(MathHelper.clamp(delayMS / 10, 0, 65535)));

        /* Setup Looping */
        IIOMetadataNode extensions = findOrCreateNode(node, "ApplicationExtensions");
        IIOMetadataNode netscape = findOrCreateNode(extensions, "ApplicationExtension");
        netscape.setAttribute("applicationID", "NETSCAPE");
        netscape.setAttribute("authenticationCode", "2.0");
        netscape.setUserObject(new byte[]{0x1, 0x0, (byte) (loop ? 0x0 : 0x1)});

        metadata.setFromTree(metadata.getNativeMetadataFormatName(), node);
    }

    private static IIOMetadataNode findOrCreateNode(IIOMetadataNode parent, String name) {
        for (int i = 0; i < parent.getLength(); i++) {
            Node child = parent.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) return (IIOMetadataNode) child;
        }

        IIOMetadataNode child = new IIOMetadataNode(name);
        parent.appendChild(child);
        return child;
    }

}
