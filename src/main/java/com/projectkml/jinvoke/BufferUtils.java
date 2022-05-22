package com.projectkml.jinvoke;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferUtils {
    public static void putUtf8String(ByteBuffer buffer, String string) {
        byte[] utf8String = string.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(utf8String.length);
        buffer.put(utf8String);
    }
}
