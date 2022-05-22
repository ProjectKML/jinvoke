package com.projectkml.jinvoke_example;

import com.projectkml.jinvoke.*;

public class Program {
    @OnlyOn(Platform.MacOS)
    @Import("libsystem.dylib")
    public static native long strlen(long str);

    @Import("test.dll")
    public static native @Pointer long getHandle(@Pointer long handle, int version);

    public static void main(String[] args) {
        try {
            JInvoke.load();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
