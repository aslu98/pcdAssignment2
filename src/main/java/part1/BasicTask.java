package part1;
import java.util.concurrent.RecursiveTask;

public abstract class BasicTask<T> extends RecursiveTask<T> {

	private String name;

	protected BasicTask(String name) {
		this.name = name;
	}

	protected void log(String msg) {
		System.out.println("[ " + this.name +"] " + Thread.currentThread() + msg);
	}
}
