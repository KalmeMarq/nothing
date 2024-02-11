package me.kalmemarq.server;

import me.kalmemarq.argoption.ArgOption;
import me.kalmemarq.argoption.ArgOptionParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        ArgOptionParser optionParser = new ArgOptionParser();
        ArgOption<String> serverIpArg = optionParser.add("serverIp", String.class).defaultsTo("localhost");
        ArgOption<Integer> serverPortArg = optionParser.add("serverPort", Integer.class).defaultsTo(8080);
        ArgOption<Boolean> showGuiArg = optionParser.add("showGui", Boolean.class).defaultsTo(true);
        ArgOption<Boolean> useStdInArg = optionParser.add("useStdIn", Boolean.class).defaultsTo(true);
        optionParser.parseArgs(args);

        new Server().startAt(
                optionParser.getValue(serverIpArg),
                optionParser.getValue(serverPortArg),
                optionParser.getValue(showGuiArg),
                optionParser.getValue(useStdInArg)
        );

//        Properties p = new Properties();
//        p.put("serverIp", "localhost");
//        p.put("serverPort", "8080");
//        p.put("showGui", "true");
//        try (FileOutputStream f = new FileOutputStream(new File("s.s"))) {
//            p.store(f, null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Properties a = new Properties();
//        try (BufferedReader reader = Files.newBufferedReader(Path.of("C:\\Users\\fmarques\\Downloads\\chat-client-server1\\s.s"))) {
//            a.load(reader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(a.get("serverPort"));
    }
}