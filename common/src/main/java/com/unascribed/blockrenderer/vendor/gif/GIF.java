/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013, 2016 NOBUOKA Yu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.unascribed.blockrenderer.vendor.gif;

import com.unascribed.blockrenderer.vendor.gif.api.IGifExtendedImageOptions;
import com.unascribed.blockrenderer.vendor.gif.api.IGifImageOptions;
import com.unascribed.blockrenderer.vendor.gif.api.IIndexedColorImage;
import com.unascribed.blockrenderer.vendor.gif.impl.LZWConverter;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Based on: <a href="https://github.com/nobuoka/GifWriter.js">GifWriter.js</a>
 */
public class GIF {

    public static void writeHeader(OutputStream os) throws IOException {
        os.write(new byte[]{'G', 'I', 'F'}); // "GIF" Signature
        os.write(new byte[]{'8', '9', 'a'}); // "89a" Version
    }

    public static void writeLogicalScreenInfo(OutputStream os, int width, int height) throws IOException {
        writeLogicalScreenInfo(os, width, height, new int[0]);
    }

    public static void writeLogicalScreenInfo(OutputStream os, int width, int height, int[] colorTableData) throws IOException {
        boolean hasColorTableData = colorTableData.length > 0;
        int colorTableSize = hasColorTableData ? calculateSizeOfColorTable(colorTableData.length) : 7;

        writeLogicalScreenDescriptor(os, width, height, hasColorTableData, false, colorTableSize, 0, 0);
        if (hasColorTableData) writeColorTable(os, colorTableData, colorTableSize);
    }

    private static int calculateSizeOfColorTable(int colorTableDataLength) {
        int numColors = colorTableDataLength / 3;

        int sct = 0;
        int v = 2;
        while (v < numColors) {
            sct++;
            v = v << 1;
        }

        return sct;
    }

    public static void writeLogicalScreenDescriptor(
            OutputStream os,
            int width, int height,
            boolean useGlobalColorTable,
            boolean colorTableSortFlag,
            int sizeOfGlobalColorTable,
            int backgroundColourIndex,
            int pixelAspectRatio
    ) throws IOException {
        writeInt2(os, width); // Logical Screen Width
        writeInt2(os, height); // Logical Screen Height

        // <Packed Field>
        os.write(
                (useGlobalColorTable ? 0x80 : 0x00)       // Global Color Table Flag (1 bit)
                        | 0x70                               // Color Resolution (3 bits) : always 7
                        | (colorTableSortFlag ? 0x08 : 0x00) // Sort Flag (1 bit)
                        | sizeOfGlobalColorTable             // Size of Global Color Table (3 bits)
        );

        os.write(backgroundColourIndex); // Background Color Index
        os.write(pixelAspectRatio);      // Pixel Apsect Ration
    }

    public static void writeColorTable(OutputStream os, int[] colorTableData, int sizeOfColorTable) throws IOException {
        for (int i : colorTableData)
            os.write(i);

        int remainder = (3 * (1 << (sizeOfColorTable + 1))) - colorTableData.length;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (--remainder >= 0) baos.write(0);
        os.write(baos.toByteArray());
    }

    public static void writeTableBasedImageWithGraphicControl(OutputStream os, IIndexedColorImage indexedColorImage, IGifExtendedImageOptions options) throws IOException {
        writeGraphicControlExtension(os, options);
        writeTableBasedImage(os, indexedColorImage, options);
    }

    public static void writeTableBasedImage(OutputStream os, IIndexedColorImage indexedColorImage) throws IOException {
        writeTableBasedImage(os, indexedColorImage, null);
    }

    public static void writeTableBasedImage(OutputStream os, IIndexedColorImage indexedColorImage, @Nullable IGifImageOptions options)
            throws IOException {
        boolean useLocalColorTable = indexedColorImage.getPaletteData().length > 0; // currently use local color table always
        int sizeOfLocalColorTable = calculateSizeOfColorTable(indexedColorImage.getPaletteData().length);

        writeImageDescriptor(os, indexedColorImage, useLocalColorTable, sizeOfLocalColorTable, options);
        if (useLocalColorTable) writeColorTable(os, indexedColorImage.getPaletteData(), sizeOfLocalColorTable);
        writeImageData(os, indexedColorImage.getData(), sizeOfLocalColorTable + 1);
    }

    public static void writeGraphicControlExtension(OutputStream os, IGifExtendedImageOptions options) throws IOException {
        int delay = options.getDelayTimeInMS() / 10;
        int disposalMethod = options.getDisposalMethod();
        boolean transparentColorFlag = options.getTransparentColorIndex() >= 0;
        int transparentColorIndex = transparentColorFlag ? options.getTransparentColorIndex() & 0xFF : 0;

        // Extension Introducer: 0x21
        // Graphic Control Label: 0xF9
        // Block Size: always this block contains 4 bytes
        os.write(new byte[]{0x21, (byte) 0xF9, 0x04});

        // <Packed Field>
        //noinspection PointlessBitwiseExpression
        os.write(
                0x00                                 // Reserved (3 bits)
                        | (disposalMethod << 2)          // Disposal Method (3 bits)
                        | 0                              // User Input Flag (1 bit)
                        | (transparentColorFlag ? 1 : 0) // Transparent Color Flag (1 bit)
        );

        writeInt2(os, delay);            // Delay Time : 1/100 sec
        os.write(transparentColorIndex); // Transparent Color Index
        writeBlockTerminator(os);        // Block Terminator
    }

    private static void writeImageDescriptor(OutputStream os, IIndexedColorImage indexedColorImage, boolean useLocalColorTable, int sizeOfLocalColorTable, @Nullable IGifImageOptions opts) throws IOException {
        // Image Separator (1 Byte) : Identifies the beginning of an Image Descriptor
        os.write(0x2C);

        // Image Left Position (2 Bytes) : Column number, in pixels, of the left edge
        //           of the image, with respect to the left edge of the Logical Screen.
        int leftPos = opts != null ? opts.getLeftPosition() : 0;
        writeInt2(os, leftPos);

        // Image Top Position (2 Bytes) : Row number, in pixels, of the top edge of
        //           the image with respect to the top edge of the Logical Screen.
        int topPos = opts != null ? opts.getTopPosition() : 0;
        writeInt2(os, topPos);

        // Image Width (2 Bytes) and Height (2 bytes)
        writeInt2(os, indexedColorImage.getWidth());
        writeInt2(os, indexedColorImage.getHeight());

        // <Packed Fields>
        //noinspection PointlessBitwiseExpression
        os.write(
                (useLocalColorTable ? 0x80 : 0x00)  // Local Color Table Flag (1 Bit)
                        | 0x00                         // Interlace Flag (1 Bit)
                        | 0x00                         // Sort Flag (1 Bit)
                        | 0x00                         // Reserved (2 Bits)
                        | sizeOfLocalColorTable        // Size of Local Color Table (3 Bits)
        );
    }

    private static void writeImageData(OutputStream os, int[] data, int numBitsForCode) throws IOException {
        // Because of some algorithmic constraints, minimum value of `numBitsForCode` is 2
        if (numBitsForCode == 1) numBitsForCode = 2;

        byte[] compressedBytes = compressWithLZW(data, numBitsForCode);

        os.write(numBitsForCode);
        writeDataSubBlocks(os, compressedBytes); // PACKAGE THE BYTES

        // GIF spec says : A block with a zero byte count terminates the
        // Raster Data stream for a given image.
        writeBlockTerminator(os);
    }

    private static void writeDataSubBlocks(OutputStream os, byte[] data) throws IOException {
        int curIdx = 0;
        int blockLastIdx = Math.min(data.length, curIdx + 254);
        while (curIdx < blockLastIdx) {
            writeDataSubBlock(os, Arrays.copyOfRange(data, curIdx, blockLastIdx));

            curIdx = blockLastIdx;
            blockLastIdx = Math.min(data.length, curIdx + 254);
        }
    }

    private static void writeDataSubBlock(OutputStream os, byte[] block) throws IOException {
        os.write((byte) block.length);
        os.write(block);
    }

    public static void writeLoopControlInfinite(OutputStream os) throws IOException {
        writeLoopControl(os, 0x00);
    }

    public static void writeLoopControl(OutputStream os, int count) throws IOException {
        os.write(new byte[]{
                0x21,        // Extension Introducer
                (byte) 0xFF, // Extension Label
                11,          // Block Size
                'N', 'E', 'T', 'S', 'C', 'A', 'P', 'E', // Application Identifier (8 Bytes) : "NETSCAPE"
                '2', '.', '0',                          // Appl. Authentication Code (3 Bytes) : "2.0"
                // Application Data
                3, // Sub-Block size
                0x01,
                (byte) (count & 0xFF), (byte) ((count >> 8) & 0xFF),
                // Block Terminator
                0x00,
        });
    }

    public static void writeTrailer(OutputStream os) throws IOException {
        os.write(';');
    }

    public static void writeBlockTerminator(OutputStream os) throws IOException {
        os.write(0x00);
    }

    private static byte[] compressWithLZW(int[] actualCodes, int numBits) {
        // `numBits` is LZW-initial code size, which indicates how many bits are needed
        // to represents actual code.

        LZWConverter bb = new LZWConverter();

        // GIF spec says: A special Clear code is defined which resets all
        // compression/decompression parameters and tables to a start-up state.
        // The value of this code is 2**<code size>. For example if the code size
        // indicated was 4 (image was 4 bits/pixel) the Clear code value would be 16
        // (10000 binary). The Clear code can appear at any point in the image data
        // stream and therefore requires the LZW algorithm to process succeeding
        // codes as if a new data stream was starting. Encoders should
        // output a Clear code as the first code of each image data stream.
        int clearCode = (1 << numBits);

        // GIF spec says: An End of Information code is defined that explicitly
        // indicates the end of the image data stream. LZW processing terminates
        // when this code is encountered. It must be the last code output by the
        // encoder for an image. The value of this code is <Clear code>+1.
        int endOfInfoCode = clearCode + 1;

        int nextCode;
        int curNumCodeBits;
        Map<String, Integer> dict;

        // resetAllParamsAndTablesToStartUpState
        nextCode = endOfInfoCode + 1;
        curNumCodeBits = numBits + 1;
        dict = new HashMap<>();

        bb.push(clearCode, curNumCodeBits); // clear code at first

        String concatedCodesKey = "";
        for (int code : actualCodes) {
            String dictKey = Character.toString((char) code);
            if (!dict.containsKey(dictKey)) dict.put(dictKey, code);

            String oldKey = concatedCodesKey;
            concatedCodesKey += dictKey;
            if (!dict.containsKey(concatedCodesKey)) {
                bb.push(dict.get(oldKey), curNumCodeBits);

                // GIF spec defines a maximum code value of 4095 (0xFFF)
                if (nextCode <= 0xFFF) {
                    dict.put(concatedCodesKey, nextCode);
                    if (nextCode == (1 << curNumCodeBits)) curNumCodeBits++;
                    nextCode++;
                } else {
                    bb.push(clearCode, curNumCodeBits);

                    // resetAllParamsAndTablesToStartUpState
                    nextCode = endOfInfoCode + 1;
                    curNumCodeBits = numBits + 1;
                    dict = new HashMap<>();

                    dict.put(dictKey, code);
                }
                concatedCodesKey = dictKey;
            }
        }
        bb.push(dict.get(concatedCodesKey), curNumCodeBits);
        bb.push(endOfInfoCode, curNumCodeBits);
        return bb.flush();
    }

    private static void writeInt2(OutputStream os, int v) throws IOException {
        os.write(new byte[]{(byte) (v & 0xFF), (byte) ((v >> 8) & 0xFF)});
    }

}
