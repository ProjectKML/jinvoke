package com.projectkml.jinvoke.util;

import com.projectkml.jinvoke.InvalidTypeException;
import org.jetbrains.annotations.NotNull;

public enum PrimitiveType {
    Byte(byte.class),
    Short(short.class),
    Int(int.class),
    Long(long.class),
    Float(float.class),
    Double(double.class),
    Pointer(long.class),
    Void(void.class);

    private final Class<?> clazz;

    private static final PrimitiveType[] values = values();

    PrimitiveType(final @NotNull Class<?> clazz) {
        this.clazz = clazz;
    }

    public static PrimitiveType fromClass(final @NotNull Class<?> clazz, final boolean isPointer) throws InvalidTypeException {
        if(isPointer) {
            if(clazz != long.class) throw new InvalidTypeException(String.format("Pointer annotation is only valid on long, not on %s", clazz.getName()));
            return PrimitiveType.Pointer;
        }

        for(final PrimitiveType type : values) {
            if(type.clazz == clazz) {
                return type;
            }
        }
        throw new InvalidTypeException(clazz);
    }
}
