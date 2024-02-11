package me.kalmemarq.client.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultResourcePack implements ResourcePack {
    private static Path resRootPath;
    private static DefaultResourcePack defaultResourcePack;

    private Path root;

    public DefaultResourcePack() {
        this.root = resRootPath;
    }

    public static Path getResourcesRootPath() {
        return resRootPath;
    }

    public static ResourcePack get() {
        if (defaultResourcePack == null) defaultResourcePack = new DefaultResourcePack();
        return defaultResourcePack;
    }

    public static boolean ensureResourcesRootPath() {
        URL resRootURL = DefaultResourcePack.class.getResource("/.res_root");
        if (resRootURL == null) return false;
        URI resRootURI;
        try {
            resRootURI = resRootURL.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        try {
            resRootPath = Path.of(resRootURI).getParent();
            return true;
        } catch (FileSystemNotFoundException e) {
        }

        try {
            FileSystems.newFileSystem(resRootURI, Collections.emptyMap());
        } catch (IOException e) {
            return false;
        }

        resRootPath = Path.of(resRootURI).getParent();
        return true;
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