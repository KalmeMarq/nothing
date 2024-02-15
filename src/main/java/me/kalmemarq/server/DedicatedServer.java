package me.kalmemarq.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DedicatedServer extends Server {
	public ServerConsoleGui gui;
	private boolean useStdIn;

	@Override
	public void printMessage(String message) {
		super.printMessage(message);
		if (this.gui != null) this.gui.textArea.append(message + "\n");
	}

	@Override
	public void startAt(String ip, int port, boolean showGui, boolean useStdIn) {
		this.useStdIn = useStdIn;
		if (showGui) this.startGui();
		if (showGui) this.gui.setOnSend(this::onSend);
		super.startAt(ip, port, showGui, useStdIn);
	}

	public void startGui() {
		if (this.gui == null) {
			this.gui = new ServerConsoleGui();
			this.gui.setOnClose(this::close);
		}
	}

	@Override
	public void run() {
		if (this.useStdIn) {
			this.startStdInThread();
		}
		super.run();
	}

	@Override
	public void close() {
		if (this.gui != null) {
			this.gui.frame.dispose();
			this.gui = null;
		}

		super.close();
	}

	public void startStdInThread() {
		Thread anotherThread = new Thread(() -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			String line;
			try {
				while (this.running.get()) {
					if ((line = reader.readLine()) == null) {
						this.running.set(false);
						break;
					}

					line = line.trim();

					if (!line.isEmpty()) {
						DedicatedServer.this.onSend(line);
					}
				}
			} catch (IOException ignored) {
			}
		});
		anotherThread.setDaemon(true);
		anotherThread.start();
	}
}
