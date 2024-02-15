package me.kalmemarq.common.logging;

import java.util.Date;
import java.util.function.Supplier;

public class SimpleLogger implements Logger {
    private final String name;

    protected SimpleLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private String formatMessage(String message, Object... args) {
        StringBuilder sb = new StringBuilder();
        int cursor = 0;
        int argIdx = 0;
        while (cursor < message.length()) {
            if (argIdx == args.length) {
                sb.append(message.substring(cursor));
                break;
            }

            if (message.charAt(cursor) == '{') {
                char chr0 = message.charAt(cursor + 1);
                if (chr0 == '}') {
                    cursor += 2;
                    sb.append(args[argIdx]);
                    ++argIdx;
                    continue;
                } else if (message.charAt(cursor + 2) == '}') {
                    if (chr0 >= '0' && chr0 <= '9') {
                        int idx = chr0 - '0';
                        if (idx < args.length) {
                            sb.append(args[idx]);
                            cursor += 3;
                        }
                        continue;
                    }
                }
            }

            sb.append(message.charAt(cursor));

            ++cursor;
        }
        return sb.toString();
    }

    private String getHeader(LogLevel level, Date date, boolean isFile) {
        if (LogManager.enableAnsi && !isFile) {
            return LogManager.Ansi.BLUE + "[" + LogManager.dateFormat.format(date) + "] " + (level == LogLevel.INFO ? LogManager.Ansi.GREEN : level == LogLevel.WARN ? LogManager.Ansi.YELLOW : LogManager.Ansi.RED) + "[" + Thread.currentThread().getName() + "/" + level.name + "]" + LogManager.Ansi.CYAN + " (" + this.name + ") " + LogManager.Ansi.RESET;
        } else {
            return "[" + LogManager.dateFormat.format(date) + "] [" + Thread.currentThread().getName() + "/" + level.name + "] (" + this.name + ") ";
        }
    }

    @Override
    public void info(Object message) {
        this.info(String.valueOf(message));
    }

    @Override
    public void info(String message) {
        if (LogManager.isLevelAllowed(LogLevel.INFO)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.INFO, date, stream.isFile()) + message + "\n");
            }
        }
    }

    @Override
    public void info(Object message, Throwable throwable) {
        this.info(String.valueOf(message), throwable);
    }

    @Override
    public void info(String message, Throwable throwable) {
        if (LogManager.isLevelAllowed(LogLevel.INFO)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.INFO, date, stream.isFile()) + message + "\n");
                throwable.printStackTrace(stream.printStream());
            }
        }
    }

    @Override
    public void info(String message, Object... params) {
        if (LogManager.isLevelAllowed(LogLevel.INFO)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.INFO, date, stream.isFile()) + this.formatMessage(message, params) + "\n");
            }
        }
    }

    @Override
    public void info(Supplier<?> messageSupplier) {
        if (!LogManager.isLevelAllowed(LogLevel.INFO)) {
            return;
        }
        this.info(messageSupplier.get());
    }

    @Override
    public void info(Supplier<?> messageSupplier, Throwable throwable) {
        if (!LogManager.isLevelAllowed(LogLevel.INFO)) {
            return;
        }
        this.info(messageSupplier.get(), throwable);
    }

    @Override
    public void warn(Object message) {
        this.warn(String.valueOf(message));
    }

    @Override
    public void warn(String message) {
        if (LogManager.isLevelAllowed(LogLevel.WARN)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.WARN, date, stream.isFile()) + message + "\n");
            }
        }
    }

    @Override
    public void warn(Object message, Throwable throwable) {
        this.warn(String.valueOf(message), throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (LogManager.isLevelAllowed(LogLevel.WARN)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.WARN, date, stream.isFile()) + message + "\n");
                throwable.printStackTrace(stream.printStream());
            }
        }
    }

    @Override
    public void warn(String message, Object... params) {
        if (LogManager.isLevelAllowed(LogLevel.WARN)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.WARN, date, stream.isFile()) + this.formatMessage(message, params) + "\n");
            }
        }
    }

    @Override
    public void warn(Supplier<?> messageSupplier) {
        if (!LogManager.isLevelAllowed(LogLevel.WARN)) {
            return;
        }
        this.warn(messageSupplier.get());
    }

    @Override
    public void warn(Supplier<?> messageSupplier, Throwable throwable) {
        if (!LogManager.isLevelAllowed(LogLevel.WARN)) {
            return;
        }
        this.warn(messageSupplier.get(), throwable);
    }

    @Override
    public void error(Object message) {
        this.error(String.valueOf(message));
    }

    @Override
    public void error(String message) {
        if (LogManager.isLevelAllowed(LogLevel.ERROR)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.ERROR, date, stream.isFile()) + message + "\n");
            }
        }
    }

    @Override
    public void error(Object message, Throwable throwable) {
        this.error(String.valueOf(message), throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (LogManager.isLevelAllowed(LogLevel.ERROR)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.ERROR, date, stream.isFile()) + message + "\n");
                throwable.printStackTrace(stream.printStream());
            }
        }
    }

    @Override
    public void error(String message, Object... params) {
        if (LogManager.isLevelAllowed(LogLevel.ERROR)) {
            Date date = new Date();

            for (LogManager.StreamEntry stream : LogManager.streams) {
                stream.printStream().print(this.getHeader(LogLevel.ERROR, date, stream.isFile()) + this.formatMessage(message, params) + "\n");
            }
        }
    }

    @Override
    public void error(Supplier<?> messageSupplier) {
        if (!LogManager.isLevelAllowed(LogLevel.ERROR)) {
            return;
        }
        this.error(messageSupplier.get());
    }

    @Override
    public void error(Supplier<?> messageSupplier, Throwable throwable) {
        if (!LogManager.isLevelAllowed(LogLevel.ERROR)) {
            return;
        }
        this.error(messageSupplier.get(), throwable);
    }
}
