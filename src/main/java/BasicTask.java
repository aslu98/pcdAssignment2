public abstract class BasicTask {

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
