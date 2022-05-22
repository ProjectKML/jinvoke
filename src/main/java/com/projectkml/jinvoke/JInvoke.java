package com.projectkml.jinvoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.Arrays;

public class JInvoke {
    private static native void generateModule(ByteBuffer buffer);

    static {
        System.out.println(Paths.get(".").toAbsolutePath());
        System.load("/Users/marlonklaus/Documents/Projekte/JInvoke/jinvoke_native/cmake-build-debug/libjinvoke_native.dylib");
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

        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());

        for(final Method method: clazz.getDeclaredMethods()) {
            final Import annotation = method.getAnnotation(Import.class);
            if(annotation == null) continue;

            final OnlyOn onlyOnAnnotation = method.getAnnotation(OnlyOn.class);
            if(onlyOnAnnotation != null && !Arrays.asList(onlyOnAnnotation.value()).contains(Platform.getCurrent())) continue;

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

            BufferUtils.putUtf8String(buffer, javaName);
            BufferUtils.putUtf8String(buffer, name);

            buffer.put((byte)returnType.ordinal());

            buffer.putInt(parameterTypes.length);
            for(PrimitiveType parameter: parameterTypes) {
                buffer.put((byte)parameter.ordinal());
            }
        }

        generateModule(buffer);
    }
}
