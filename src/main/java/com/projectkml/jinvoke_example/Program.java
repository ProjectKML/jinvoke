package com.projectkml.jinvoke_example;

import com.projectkml.jinvoke.*;
import com.projectkml.jinvoke.util.Platform;

public class Program {
    @OnlyOn(Platform.MacOS)
    @Import("libsystem.dylib")
    public static native long strlen(long str);

    @Import("test.dll")
    public static native @Pointer long getHandle(@Pointer long handle, int version);

    @Import("test.dll")
    public static native byte testFunction(long l, @Pointer long v, int a, int b, short c);

    public static void main(String[] args) {
        try {
            JInvoke.load();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
