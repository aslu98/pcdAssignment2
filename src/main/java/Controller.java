

import java.io.File;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements InputListener {

	private Flag stopFlag;
	private View view;

	public Controller(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}

	public synchronized void started(File dir, File wordsFile, int nMostFreqWords){
		stopFlag.reset();
		Master master = new Master(wordsFile, dir, nMostFreqWords,stopFlag, view);
		master.compute();
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
