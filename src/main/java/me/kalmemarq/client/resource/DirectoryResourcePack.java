package me.kalmemarq.client.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DirectoryResourcePack implements ResourcePack {
    private final Path root;

    public DirectoryResourcePack(Path root) {
        this.root = root;
    }

    @Override
    public Optional<Resource> getResource(String path) {
        if (this.hasResource(path) && !Files.isDirectory(this.root.resolve(path))) {
            return Optional.of(this.createResource(path, () -> Files.newInputStream(this.root.resolve(path))));
        }
        return Optional.empty();
    }

    @Override
    public boolean hasResource(String path) {
        return Files.exists(this.root.resolve(path));
    }

	@Override
	public List<Resource> getAllResources() {
		return this.getAllResources(filePath -> true);
	}

	@Override
	public List<Resource> getAllResources(Predicate<String> filter) {
		List<Resource> list = new ArrayList<>();

		recursiveInto(this.root, file -> {
		 	if (filter.test(file.toString())) list.add(this.createResource(file.toString(), () -> Files.newInputStream(file)));
		});

		return list;
	}

	@Override
    public List<Resource> getAllResources(String path) {
        return this.getAllResources(path, filePath -> true);
    }

	@Override
	public List<Resource> getAllResources(String path, Predicate<String> filter) {
		List<Resource> list = new ArrayList<>();

		recursiveInto(this.root.resolve(path), file -> {
			if (filter.test(file.toString())) list.add(this.createResource(file.toString(), () -> Files.newInputStream(file)));
		});

		return list;
	}
	
	private static void recursiveInto(Path path, Consumer<Path> consumer) {
		try {
			for (Path item : Files.list(path).toList()) {
				if (Files.isDirectory(item)) {
					recursiveInto(item, consumer);
				} else {
					consumer.accept(item);
				}
			}
		} catch (IOException ignored) {
		}
	}
}
