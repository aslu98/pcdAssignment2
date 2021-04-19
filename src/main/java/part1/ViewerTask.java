package part1;

import java.util.Arrays;

public class ViewerTask extends BasicTask<Void> {

	private WordFreqMap map;
	private View view;
	private Flag done;
	
	protected ViewerTask(WordFreqMap map, View view, Flag done) {
		super("viewer");
		this.map = map;
		this.view = view;
		this.done = done;
	}

	public Void compute() {
		log("started.");
		while (!done.isSet()) {
			try {
				view.update(map.getCurrentMostFreq());
				Thread.sleep(10);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(map.getCurrentMostFreq());
		return null;
	}
}
