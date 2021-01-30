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
package com.unascribed.blockrenderer.vendor.gif.impl;

import java.io.ByteArrayOutputStream;

public class LZWConverter {

    private final ByteArrayOutputStream os = new ByteArrayOutputStream();
    private int remNumBits = 0;
    private int remVal = 0;

    public void push(int code, int numBits) {
        while (numBits > 0) {
            remVal = ((code << remNumBits) & 0xFF) + remVal;

            if (numBits + remNumBits >= 8) {
                os.write(remVal);

                numBits = numBits - (8 - remNumBits);
                code = (code >> (8 - remNumBits));

                remVal = 0;
                remNumBits = 0;
            } else {
                remNumBits = numBits + remNumBits;
                numBits = 0;
            }
        }
    }

    public byte[] flush() {
        push(0, 8);
        remNumBits = 0;
        remVal = 0;
        byte[] out = os.toByteArray();
        os.reset();
        return out;
    }

}
