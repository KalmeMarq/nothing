package me.kalmemarq.client.resource;

import java.io.IOException;
import java.io.InputStream;

public record Resource(String path, ResourcePack pack, ResourcePack.InputSupplier inputSupplier) {
	public InputStream getInputStream() throws IOException {
		return this.inputSupplier.get();
	}
}
