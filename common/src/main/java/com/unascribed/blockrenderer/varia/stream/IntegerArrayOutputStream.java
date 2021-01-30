package com.unascribed.blockrenderer.varia.stream;

import java.io.OutputStream;
import java.util.Arrays;

public class IntegerArrayOutputStream extends OutputStream {

    private int[] buffer;
    private int count;

    public IntegerArrayOutputStream() {
        this(32);
    }

    public IntegerArrayOutputStream(int size) {
        if (size < 0) throw new IllegalArgumentException("Initial Size must be positive: " + size);
        buffer = new int[size];
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity - buffer.length > 0) grow(minCapacity);
    }

    private void grow(int minCapacity) {
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) newCapacity = minCapacity;
        buffer = Arrays.copyOf(buffer, newCapacity);
    }

    @Override
    public synchronized void write(int data) {
        ensureCapacity(count + 1);
        buffer[count] = data;
        count += 1;
    }

    public synchronized void write(int[] data, int offset, int length) {
        if ((offset < 0) || (offset > data.length) || (length < 0) || ((offset + length) - data.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + length);
        System.arraycopy(data, offset, buffer, count, length);
        count += length;
    }

    public synchronized void reset() {
        count = 0;
    }

    public synchronized int[] toArray() {
        return Arrays.copyOf(buffer, count);
    }

    public synchronized int size() {
        return count;
    }

    @Override
    public synchronized String toString() {
        return new String(buffer, 0, count);
    }

    @Override
    public void close() {

    }

}
