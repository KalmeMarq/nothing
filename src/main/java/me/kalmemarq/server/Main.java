package me.kalmemarq.server;

import me.kalmemarq.argoption.ArgOption;
import me.kalmemarq.argoption.ArgOptionParser;
import me.kalmemarq.common.logging.LogManager;

public class Main {
    public static void main(String[] args) {
		LogManager.addStream(System.out);
		
        ArgOptionParser optionParser = new ArgOptionParser();
        ArgOption<String> serverIpArg = optionParser.add("serverIp", String.class).defaultsTo("localhost");
        ArgOption<Integer> serverPortArg = optionParser.add("serverPort", Integer.class).defaultsTo(8080);
        ArgOption<Boolean> showGuiArg = optionParser.add("showGui", Boolean.class).defaultsTo(true);
        ArgOption<Boolean> useStdInArg = optionParser.add("useStdIn", Boolean.class).defaultsTo(true);
        optionParser.parseArgs(args);

        new DedicatedServer().startAt(
                optionParser.getValue(serverIpArg),
                optionParser.getValue(serverPortArg),
                optionParser.getValue(showGuiArg),
                optionParser.getValue(useStdInArg)
        );
    }
}
