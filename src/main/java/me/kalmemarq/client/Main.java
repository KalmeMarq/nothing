package me.kalmemarq.client;

import me.kalmemarq.argoption.ArgOption;
import me.kalmemarq.argoption.ArgOptionParser;
import me.kalmemarq.client.resource.DefaultResourcePack;
import org.lwjgl.system.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
		Configuration.DEBUG.set(true);
		Configuration.DEBUG_LOADER.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);
		Configuration.DEBUG_STREAM.set(true);
		
        ArgOptionParser optionParser = new ArgOptionParser();
        ArgOption<Path> saveDirArg = optionParser.add("saveDir", Path.class).defaultsTo(Path.of("."));
        optionParser.parseArgs(args);

        if (!DefaultResourcePack.ensureResourcesRootPath()) {
            System.err.println("Could not get path to jar resources folder");
            return;
        }

        Path saveDirPath = optionParser.getValue(saveDirArg);

        try {
            if (!Files.exists(saveDirPath)) {
                Files.createDirectories(saveDirPath);
            }
        } catch (IOException e) {
			System.out.println("Failed to ensure save directory existence");
            return;
        }

		Thread.currentThread().setName("Main Thread");
        new Client(saveDirPath).run();
    }
}
