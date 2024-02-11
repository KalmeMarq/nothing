package me.kalmemarq.client.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

public class DirectoryResourcePack implements ResourcePack {
    private final Path root;

    public DirectoryResourcePack(Path root) {
        this.root = root;
    }

    @Override
    public Optional<InputSupplier> get(String path) {
        if (this.has(path) && !Files.isDirectory(this.root.resolve(path))) {
            return Optional.of(() -> Files.newInputStream(this.root.resolve(path)));
        }
        return Optional.empty();
    }

    @Override
    public boolean has(String path) {
        return Files.exists(this.root.resolve(path));
    }

    @Override
    public List<InputSupplier> getAll(String path) {
        return Collections.emptyList();
    }
}
