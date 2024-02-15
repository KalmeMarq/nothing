package me.kalmemarq.client.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
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
				return true;
            } catch (IOException e) {
				e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    @Override
    public Optional<Resource> getResource(String path) {
        return Optional.empty();
    }

    @Override
    public boolean hasResource(String path) {
        return false;
    }

	@Override
	public List<Resource> getAllResources() {
		if (!this.ensureZipFile()) return Collections.emptyList();

		List<Resource> list = new ArrayList<>();

		this.zipFile.stream().forEach(entry -> {
			if (!entry.isDirectory()) {
				list.add(this.createResource(entry.getName(), () -> this.zipFile.getInputStream(entry)));
			}
		});

		return list;
	}

	@Override
	public List<Resource> getAllResources(Predicate<String> filter) {
		if (!this.ensureZipFile()) return Collections.emptyList();

		List<Resource> list = new ArrayList<>();

		this.zipFile.stream().forEach(entry -> {
			if (filter.test(entry.getName())) {
				if (!entry.isDirectory()) {
					list.add(this.createResource(entry.getName(), () -> this.zipFile.getInputStream(entry)));
				}
			}
		});

		return list;
	}

	@Override
    public List<Resource> getAllResources(String path) {
		if (!this.ensureZipFile()) return Collections.emptyList();

		List<Resource> list = new ArrayList<>();

		this.zipFile.stream().forEach(entry -> {
			if (!entry.isDirectory() && entry.getName().startsWith(path)) {
				list.add(this.createResource(entry.getName(), () -> this.zipFile.getInputStream(entry)));
			}
		});

		return list;
    }

	@Override
	public List<Resource> getAllResources(String path, Predicate<String> filter) {
		if (!this.ensureZipFile()) return Collections.emptyList();

		List<Resource> list = new ArrayList<>();

		this.zipFile.stream().forEach(entry -> {
			if (!entry.isDirectory() && entry.getName().startsWith(path)) {
				if (filter.test(entry.getName())) {
                    list.add(this.createResource(entry.getName(), () -> this.zipFile.getInputStream(entry)));
                }
			}
		});

		return list;
	}
}
