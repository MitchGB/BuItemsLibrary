package com.bu.buitemslibrary.util;

import org.bukkit.event.Event;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ReflectionUtil {
    static Consumer<String> toConsumer(MethodHandles.Lookup lookup, Object annotated, Method method) throws Throwable {
        MethodType consumeString = MethodType.methodType(void.class, String.class);
        MethodHandle handle = lookup.unreflect(method);
        final CallSite site = LambdaMetafactory.metafactory(lookup, "accept",
                MethodType.methodType(Consumer.class, annotated.getClass()),
                consumeString.changeParameterType(0, Object.class),
                handle,
                consumeString);
        return (Consumer<String>) site.getTarget().invoke(annotated);
    }

    public static <T extends Event> Consumer<T> toConsumer(Class<T> clazz, MethodHandles.Lookup lookup, Object instance, Method method){
        return null;
    }
}
