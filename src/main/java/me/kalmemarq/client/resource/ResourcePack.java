package me.kalmemarq.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface ResourcePack {
    Optional<InputSupplier> get(String path);
    boolean has(String path);
    List<InputSupplier> getAll(String path);

    @FunctionalInterface
    interface InputSupplier {
        InputStream get() throws IOException;
    }
}
