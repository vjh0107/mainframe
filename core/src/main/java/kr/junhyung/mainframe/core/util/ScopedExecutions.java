package kr.junhyung.mainframe.core.util;

import java.util.function.Supplier;

public final class ScopedExecutions {

    private ScopedExecutions() {
    }

    public static <T> T withContextClassLoader(ClassLoader classLoader, Supplier<T> action) {
        Thread currentThread = Thread.currentThread();
        ClassLoader original = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(classLoader);
        try {
            return action.get();
        } finally {
            currentThread.setContextClassLoader(original);
        }
    }

    public static <T> T withSystemProperty(String key, String value, Supplier<T> action) {
        String original = System.getProperty(key);
        System.setProperty(key, value);
        try {
            return action.get();
        } finally {
            restoreSystemProperty(key, original);
        }
    }

    private static void restoreSystemProperty(String key, String originalValue) {
        if (originalValue == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, originalValue);
        }
    }

}