import java.util.Map;
import java.util.concurrent.RecursiveTask;

public abstract class BasicTask extends RecursiveTask<WordFreqMap> {

	private String name;

	protected BasicTask(String name) {
		this.name = name;
	}

	protected String getName(){
		return this.name;
	}

	protected void log(String msg) {
		System.out.println("[ " + this.name +"] " + msg);
	}
}
