package com.projectkml.jinvoke.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class LibraryUtils {
    private static final Random RANDOM = ThreadLocalRandom.current();
    private static final List<File> LIBRARIES = new ArrayList<>();

    // @formatter:off
    private LibraryUtils() {}
    // @formatter:on

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (File library : LIBRARIES) {
                library.delete();
            }
        }, "Library Deleter"));
    }

    public static void loadByteArray(final byte[] data) {
        try {
            final File current = File.createTempFile("jinvoke-" + RANDOM.nextLong(), "dll");
            final OutputStream outputStream = new FileOutputStream(current);
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();

            LIBRARIES.add(current);
            System.load(current.getAbsolutePath());
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
