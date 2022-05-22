package com.projectkml.jinvoke.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DynamicByteBuffer {
    private byte[] buffer;
    private int size;

    private void ensureCapacity(int toAdd) {
        int requestedCapacity = size + toAdd;
        if(requestedCapacity > buffer.length) {
            int newCapacity = buffer.length + (buffer.length >> 1);
            if(requestedCapacity > newCapacity) {
                newCapacity = requestedCapacity;
            }

            final byte[] newBytes = new byte[newCapacity];
            System.arraycopy(buffer, 0, newBytes, 0, size);
            buffer = newBytes;
        }
    }

    private void writeUnchecked(final byte b) {
        buffer[size++] = b;
    }

    public DynamicByteBuffer(int initialCapacity) {
        buffer = new byte[initialCapacity];
    }

    public void write(final byte b) {
        ensureCapacity(1);

        writeUnchecked(b);
    }

    public void write(final int i) {
        ensureCapacity(4);

        if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            writeUnchecked((byte) (i & 0xFF));
            writeUnchecked((byte) ((i >> 8) & 0xFF));
            writeUnchecked((byte) ((i >> 16) & 0xFF));
            writeUnchecked((byte) ((i >> 24) & 0xFF));
        } else {
            writeUnchecked((byte) ((i >> 24) & 0xFF));
            writeUnchecked((byte) ((i >> 16) & 0xFF));
            writeUnchecked((byte) ((i >> 8) & 0xFF));
            writeUnchecked((byte) (i & 0xFF));
        }
    }

    public void write(final @NotNull String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

        write(bytes.length);

        ensureCapacity(bytes.length);
        for(int i = 0; i < bytes.length; i++) {
            writeUnchecked(bytes[i]);
        }
    }

    public ByteBuffer createByteBuffer() {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        buffer.put(this.buffer, 0, size);
        buffer.flip();

        return buffer;
    }
}
