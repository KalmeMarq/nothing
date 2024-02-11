package me.kalmemarq.client.resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

public class ZipResourcePack implements ResourcePack {
    private final Path zipFilePath;
    private ZipFile zipFile;

    public ZipResourcePack(Path zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public boolean ensureZipFile() {
        if (this.zipFile == null) {
            try {
                this.zipFile = new ZipFile(this.zipFilePath.toFile());
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public Optional<InputSupplier> get(String path) {
        return Optional.empty();
    }

    @Override
    public boolean has(String path) {
        return false;
    }

    @Override
    public List<InputSupplier> getAll(String path) {
        return Collections.emptyList();
    }
}
