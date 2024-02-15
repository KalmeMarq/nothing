package me.kalmemarq.client.resource;

import me.kalmemarq.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ResourcePack {
	default Resource createResource(String path, InputSupplier inputSupplier) {
		return new Resource(path, this, inputSupplier);
	}
	
	default Optional<Resource> getResource(Identifier identifier) {
		return this.getResource("assets/" + identifier.getNamespace() + "/" + identifier.getPath());
	}
	
    Optional<Resource> getResource(String path);
    
	boolean hasResource(String path);
	
    List<Resource> getAllResources();

	List<Resource> getAllResources(Predicate<String> filter);

	List<Resource> getAllResources(String path);

	List<Resource> getAllResources(String path, Predicate<String> filter);

    @FunctionalInterface
    interface InputSupplier {
        InputStream get() throws IOException;
    }
}
