package com.example.roottools.utils;

public class Logger {

    public interface Listener {
        void onLog(int level, String tag, String msg);
    }

    private static Logger.Listener listener = null;

    public static void setListener(Listener listener) {
        Logger.listener = listener;
    }

    public static final void log(String tag, String msg) {
        if (listener != null) {
            listener.onLog(0, tag, msg);
        }
    }

    public static final void log(String msg) {
        if (listener != null) {
            listener.onLog(0, "", msg);
        }
    }
}
