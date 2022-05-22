package com.projectkml.jinvoke;

import com.projectkml.jinvoke.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Arrays;

public class JInvoke {
    private static native void generateModule(ByteBuffer buffer);

    static {
        System.load(Paths.get(".", "jinvoke_native/target/release/libjinvoke_native.so").toAbsolutePath().toString());
    }

    public static void load() throws InvalidTypeException {
        Class<?> clazz = null;
        try {
            final String name = Thread.currentThread().getStackTrace()[2].getClassName();
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final Platform currentPlatform = Platform.getCurrent();
        final DynamicByteBuffer buffer = new DynamicByteBuffer(512);

        for(final Method method: clazz.getDeclaredMethods()) {
            final Import annotation = method.getAnnotation(Import.class);
            if(annotation == null) {
                continue;
            }

            final OnlyOn onlyOnAnnotation = method.getAnnotation(OnlyOn.class);
            if(onlyOnAnnotation != null && !ArrayUtils.contains(onlyOnAnnotation.value(), currentPlatform)) {
                continue;
            }

            final String javaName = String.format("Java_%s_%s", clazz.getName().replace("_", "1_").replace('.', '_'),
                    method.getName().replace("_", "1_"));
            final String name = (annotation.name().isEmpty() ? method.getName() : annotation.name());

            final PrimitiveType returnType = PrimitiveType.fromClass(method.getReturnType(), method.isAnnotationPresent(Pointer.class));
            final PrimitiveType[] parameterTypes = new PrimitiveType[method.getParameterCount()];

            for(int i = 0; i < method.getParameterCount(); i++) {
                boolean isPointer = false;

                for(final Annotation paramAnnotation : method.getParameterAnnotations()[i]) {
                    if(paramAnnotation instanceof Pointer) {
                        isPointer = true;
                        break;
                    }
                }
                parameterTypes[i] = PrimitiveType.fromClass(method.getParameterTypes()[i], isPointer);
            }

            buffer.write(javaName);
            buffer.write(name);

            buffer.write((byte)returnType.ordinal());

            buffer.write(parameterTypes.length);
            for(PrimitiveType parameter: parameterTypes) {
                buffer.write((byte)parameter.ordinal());
            }
        }

        generateModule(buffer.createByteBuffer());
    }
}
