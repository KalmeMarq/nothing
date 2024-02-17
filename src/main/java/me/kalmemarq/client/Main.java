package me.kalmemarq.client;

import me.kalmemarq.argoption.ArgOption;
import me.kalmemarq.argoption.ArgOptionParser;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.common.logging.LogManager;
import org.lwjgl.system.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
		LogManager.addStream(System.out);
		
        ArgOptionParser optionParser = new ArgOptionParser();
		ArgOption<Boolean> debugModeArg = optionParser.add("debugMode", Boolean.class).defaultsTo(false);
        ArgOption<Path> saveDirArg = optionParser.add("saveDir", Path.class).defaultsTo(Path.of("."));
        optionParser.parseArgs(args);

		if (optionParser.getValue(debugModeArg)) {
			Configuration.DEBUG_LOADER.set(true);
			Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
			Configuration.DEBUG_STACK.set(true);
			Configuration.DEBUG_STREAM.set(true);
		}

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
        new Client(optionParser.getValue(debugModeArg), saveDirPath).run();
    }
}
