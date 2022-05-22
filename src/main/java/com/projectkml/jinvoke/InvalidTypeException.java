package com.projectkml.jinvoke;

import org.jetbrains.annotations.NotNull;

public class InvalidTypeException extends Exception {
    public InvalidTypeException(final @NotNull Class<?> clazz) {
        super(String.format("Invalid type: %s", clazz.getName()));
    }

    public InvalidTypeException(final @NotNull String message) {
        super(message);
    }
}
