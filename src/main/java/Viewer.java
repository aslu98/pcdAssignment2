import java.util.concurrent.Callable;

public class Viewer extends Thread {

	private WordFreqMap map;
	private View view;
	private Flag done;
	
	protected Viewer(WordFreqMap map, View view, Flag done) {
		super("viewer");
		this.map = map;
		this.view = view;
		this.done = done;
	}

	public void run() {
		System.out.println(super.getName() + "started.");
		while (!done.isSet()) {
			try {
				view.update(map.getCurrentMostFreq());
				Thread.sleep(10);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(map.getCurrentMostFreq());
	}
}
