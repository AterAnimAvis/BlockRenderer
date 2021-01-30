package com.unascribed.blockrenderer.varia.gif;

import com.unascribed.blockrenderer.vendor.gif.GIF;
import com.unascribed.blockrenderer.vendor.gif.api.DisposalMethod;
import com.unascribed.blockrenderer.vendor.gif.impl.GifExtendedImageOptions;
import com.unascribed.blockrenderer.vendor.gif.impl.GifImageOptions;
import com.unascribed.blockrenderer.vendor.gif.impl.IndexedColorImage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GIFTest {

    @Test
    void writesHeader() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeHeader(baos);

        assertArrayEquals(new byte[]{'G', 'I', 'F', '8', '9', 'a'}, baos.toByteArray());
    }

    @Test
    void writesLogicalScreenInfo() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeLogicalScreenInfo(baos, 2, 1);

        assertEquals(4 + 3, baos.size());
        assertArrayEquals(new byte[]{
                0x02, 0x00, 0x01, 0x00,  // First 4 bytes represents width and height
                (byte) 0x77, 0x00, 0x00, // Next 3 bytes is default value
        }, baos.toByteArray());
    }

    @Test
    void writesLogicalScreenInfoWithColorTable() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeLogicalScreenInfo(baos, 2, 1, new int[]{0, 1, 2, 50, 51, 52, 253, 254, 255});

        assertEquals(4 + 3 + (4 * 3), baos.size());
        assertArrayEquals(new byte[]{
                0x02, 0x00, 0x01, 0x00,  // First 4 bytes represents width and height
                (byte) 0xF1, 0x00, 0x00, // Next 3 bytes is default value
                0, 1, 2, 50, 51, 52, (byte) 253, (byte) 254, (byte) 255, 0x00, 0x00, 0x00 // Followed by the ColorTableData (padded by 0's)
        }, baos.toByteArray());
    }

    @Test
    void writesTableBasedImage() throws IOException { // TODO: Cleanup Following Tests
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IndexedColorImage indexedColorImage = new IndexedColorImage(2, 2, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0});

        GIF.writeTableBasedImage(baos, indexedColorImage);

        assertArrayEquals(new byte[]{
                0x2C, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x02, 0x00, (byte) 0x80, // Image Descriptor
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Color Table
                // Image Data
                0x02, // initial code size
                0x02, // block size
                (byte) 0x84, // (10 000 100) binary
                0x51, // (0 101 000 1) binary
                0x00  // terminates blocks
        }, baos.toByteArray());
    }

    @Test
    void writesTableBasedImagePositional() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IndexedColorImage indexedColorImage = new IndexedColorImage(2, 2, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0});

        GIF.writeTableBasedImage(baos, indexedColorImage, new GifImageOptions(5, 3));

        assertArrayEquals(new byte[]{
                0x2C, 0x03, 0x00, 0x05, 0x00, 0x02, 0x00, 0x02, 0x00, (byte) 0x80, // Image Descriptor
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Color Table
                // Image Data
                0x02, // initial code size
                0x02, // block size
                (byte) 0x84, // (10 000 100) binary
                0x51, // (0 101 000 1) binary
                0x00  // terminates blocks
        }, baos.toByteArray());
    }

    @Test
    void writesTableBasedImageWithGraphicControl() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IndexedColorImage indexedColorImage = new IndexedColorImage(2, 2, new int[]{0, 0, 0, 0}, new int[]{0, 0, 0});

        GIF.writeTableBasedImageWithGraphicControl(baos, indexedColorImage, new GifExtendedImageOptions(5, 3, 0, DisposalMethod.RESTORE_TO_BACKGROUND, -1));

        assertArrayEquals(new byte[]{
                0x21,        // Extension Introducer: 0x21
                (byte) 0xF9, // Graphic Control Label: 0xF9
                0x04,        // Block Size: always this block contains 4 bytes
                0x08,        // Disposal Method : 2, User Input Flag : 0, Transparent Color Flag : 0
                0x00, 0x00,  // Delay Time : 0
                0x00,        // Transparent Color Index : 0
                0x00,        // Block Terminator
                0x2C, 0x03, 0x00, 0x05, 0x00, 0x02, 0x00, 0x02, 0x00, (byte) 0x80, // Image Descriptor
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Color Table
                // Image Data
                0x02, // initial code size
                0x02, // block size
                (byte) 0x84, // (10 000 100) binary
                0x51, // (0 101 000 1) binary
                0x00  // terminates blocks
        }, baos.toByteArray());
    }

    @Test
    void writesGraphicControlExtension() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeGraphicControlExtension(baos, new GifExtendedImageOptions(0, 0, 0, DisposalMethod.RESTORE_TO_BACKGROUND, -1));

        assertArrayEquals(new byte[]{
                0x21,        // Extension Introducer: 0x21
                (byte) 0xF9, // Graphic Control Label: 0xF9
                0x04,        // Block Size: always this block contains 4 bytes
                0x08,        // Disposal Method : 2, User Input Flag : 0, Transparent Color Flag : 0
                0x00, 0x00,  // Delay Time : 0
                0x00,        // Transparent Color Index : 0
                0x00         // Block Terminator
        }, baos.toByteArray());
    }

    @Test
    void writesGraphicControlExtensionDelay() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeGraphicControlExtension(baos, new GifExtendedImageOptions(0, 0, 1000, DisposalMethod.RESTORE_TO_BACKGROUND, -1));

        assertArrayEquals(new byte[]{
                0x21,        // Extension Introducer: 0x21
                (byte) 0xF9, // Graphic Control Label: 0xF9
                0x04,        // Block Size: always this block contains 4 bytes
                0x08,        // Disposal Method : 2, User Input Flag : 0, Transparent Color Flag : 0
                0x64, 0x00,  // Delay Time : 100
                0x00,        // Transparent Color Index : 0
                0x00         // Block Terminator
        }, baos.toByteArray());
    }

    @Test
    void writesGraphicControlExtensionTransparent() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeGraphicControlExtension(baos, new GifExtendedImageOptions(0, 0, 0, DisposalMethod.RESTORE_TO_BACKGROUND, 1));

        assertArrayEquals(new byte[]{
                0x21,        // Extension Introducer: 0x21
                (byte) 0xF9, // Graphic Control Label: 0xF9
                0x04,        // Block Size: always this block contains 4 bytes
                0x09,        // Disposal Method : 2, User Input Flag : 0, Transparent Color Flag : 1
                0x00, 0x00,  // Delay Time : 0
                0x01,        // Transparent Color Index : 1
                0x00         // Block Terminator
        }, baos.toByteArray());
    }

    @Test
    void writesGraphicControlExtensionDisposal() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeGraphicControlExtension(baos, new GifExtendedImageOptions(0, 0, 0, DisposalMethod.RESTORE_TO_PREVIOUS, -1));

        assertArrayEquals(new byte[]{
                0x21,        // Extension Introducer: 0x21
                (byte) 0xF9, // Graphic Control Label: 0xF9
                0x04,        // Block Size: always this block contains 4 bytes
                0x0C,        // Disposal Method : 3, User Input Flag : 0, Transparent Color Flag : 0
                0x00, 0x00,  // Delay Time : 0
                0x00,        // Transparent Color Index : 0
                0x00         // Block Terminator
        }, baos.toByteArray());
    }

    @Test
    void writesGraphicControlExtensionDisposalTransparent() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeGraphicControlExtension(baos, new GifExtendedImageOptions(0, 0, 0, DisposalMethod.RESTORE_TO_PREVIOUS, 1));

        assertArrayEquals(new byte[]{
                0x21,        // Extension Introducer: 0x21
                (byte) 0xF9, // Graphic Control Label: 0xF9
                0x04,        // Block Size: always this block contains 4 bytes
                0x0D,        // Disposal Method : 3, User Input Flag : 0, Transparent Color Flag : 1
                0x00, 0x00,  // Delay Time : 0
                0x01,        // Transparent Color Index : 1
                0x00         // Block Terminator
        }, baos.toByteArray());
    }

    @Test
    void writesLoopControl() throws IOException {
        writesLoopControl(0x0000, 0x00, 0x00);
        writesLoopControl(0x0001, 0x01, 0x00);
        writesLoopControl(0x0010, 0x10, 0x00);
        writesLoopControl(0x0100, 0x00, 0x01);
        writesLoopControl(0x1000, 0x00, 0x10);
    }

    @Test
    void writesLoopControlInfinite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeLoopControlInfinite(baos);

        checkLoopControl(baos, 0x00, 0x00);
    }

    void writesLoopControl(int count, int a, int b) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeLoopControl(baos, count);

        checkLoopControl(baos, a, b);
    }

    void checkLoopControl(ByteArrayOutputStream baos, int a, int b) {
        assertArrayEquals(new byte[]{
                // Fixed value
                0x21, (byte) 0xFF, 11, 'N', 'E', 'T', 'S', 'C', 'A', 'P', 'E', '2', '.', '0', 3, 0x01,
                // according to loop count
                (byte) a, (byte) b,
                // Fixed value
                0x00,
        }, baos.toByteArray());
    }

    @Test
    void writesTrailer() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeTrailer(baos);

        assertArrayEquals(new byte[]{';'}, baos.toByteArray());
    }

    @Test
    void writesBlockTerminator() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GIF.writeBlockTerminator(baos);

        assertArrayEquals(new byte[]{0x00}, baos.toByteArray());
    }

}