package me.kalmemarq.client;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import me.kalmemarq.argoption.ArgOption;
import me.kalmemarq.argoption.ArgOptionParser;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.common.logging.LogManager;
import org.lwjgl.system.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

public class Main {
    public static void main(String[] args) throws InterruptedException, NoDiscordClientException, IOException {
		LogManager.addStream(System.out);
		
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
