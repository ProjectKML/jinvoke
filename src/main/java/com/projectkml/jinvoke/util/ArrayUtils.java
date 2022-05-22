package com.projectkml.jinvoke.util;

public final class ArrayUtils {
    // @formatter:off
    private ArrayUtils() {}
    // @formatter:on

    public static <T> boolean contains(final T[] data, final T element) {
        for(int i = 0; i < data.length; i++) {
            if(data[i].equals(element)) {
                return true;
            }
        }

        return false;
    }
}
