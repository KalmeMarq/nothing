package me.kalmemarq.common.logging;

import java.util.function.Supplier;

public interface Logger {
    /**
     * Returns the name of this logger.
     * @return the name of this logger
     */
    String getName();

    void info(Object message);

    void info(String message);

    void info(Object message, Throwable throwable);

    void info(String message, Throwable throwable);

    void info(String message, Object... params);

    void info(Supplier<?> messageSupplier);

    void info(Supplier<?> messageSupplier, Throwable throwable);

    void warn(Object message);

    void warn(String message);

    void warn(Object message, Throwable throwable);

    void warn(String message, Throwable throwable);

    void warn(String message, Object... params);

    void warn(Supplier<?> messageSupplier);

    void warn(Supplier<?> messageSupplier, Throwable throwable);

    void error(Object message);

    void error(String message);

    void error(Object message, Throwable throwable);

    void error(String message, Throwable throwable);

    void error(String message, Object... params);

    void error(Supplier<?> messageSupplier);

    void error(Supplier<?> messageSupplier, Throwable throwable);

    enum LogLevel {
        OFF(""),
        INFO("Info"),
        WARN("Warn"),
        ERROR("Error");

        public final String name;

        LogLevel(final String name) {
            this.name = name;
        }
    }
}
