package com.snoworca.fanCtrl;


import java.io.Closeable;

public class SafeClose {
    public static final void execute(Object closeable) {
        if(closeable == null) return;
        try {
            if (closeable instanceof Closeable) {
                ((Closeable) closeable).close();
            } else {
                try {
                    closeable.getClass().getMethod("close").invoke(closeable);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (Exception ignored) {

        }
    }
}
