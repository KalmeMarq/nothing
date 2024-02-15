package me.kalmemarq;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public abstract class ThreadExecutor implements Executor {
	private final Queue<Runnable> runnables = new ConcurrentLinkedQueue<>();
	
	public boolean isOnThread() {
		return this.getThread() == Thread.currentThread();
	}
	
	public abstract Thread getThread();
	
	@Override
	public void execute(Runnable command) {
		if (!this.isOnThread()) {
			this.runnables.add(command);
		} else {
			command.run();
		}
	}

	/**
	 * Run a pending task. Returns {@code false} if queue is empty and no task was executed and {@code true} if task was run.
	 * @return {@code false} if queue is empty and no task was executed and {@code true} if task was run.
	 */
	public boolean runTask() {
		var command = this.runnables.peek();
		if (command == null) return false;
		command.run();
		this.runnables.remove();
		return true;
	}

	/**
	 * Run any pending tasks.
	 */
	public void runTasks() {
		while (true) {
			if (!this.runTask()) break;
		}
	}

	public void runTasks(Supplier<Boolean> stopCondition) {
		while (stopCondition.get()) {
			if (!this.runTask()) break;
		}
	}
}
