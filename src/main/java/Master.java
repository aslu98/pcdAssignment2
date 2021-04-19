import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class Master {

	private File configFile;
	private File dir;
	private int numMostFreqWords;
	
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	private final ForkJoinPool forkJoinPool;
	
	private Flag stopFlag;

	private View view;

	public Master(File configFile, File dir, int numMostFreqWords, Flag stopFlag, View view) {
		this.configFile = configFile;
		this.dir = dir;
		this.numMostFreqWords = numMostFreqWords;
		this.view = view;
		this.stopFlag = stopFlag;
		this.forkJoinPool = new ForkJoinPool();
	}

	public void compute() {
		try {
			log("started ");
			long t0 = System.currentTimeMillis();

			map = new WordFreqMap(numMostFreqWords);

			Flag done = new Flag();
			Viewer viewer = new Viewer(map,view,done);
			viewer.start();

			loadWordsToDiscard(configFile);

			map = forkJoinPool.invoke(new DocDiscoverer(dir, stopFlag, wordsToDiscard, map));
						
			long t2 = System.currentTimeMillis();
			done.set();
			view.done();
			
			elabMostFreqWords();
			
			log("done in " + (t2-t0));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	private void loadWordsToDiscard(File configFile) {
		try {
			wordsToDiscard = new HashMap<String,String>();
			FileReader fr = new FileReader(configFile);
			BufferedReader br = new BufferedReader(fr);
			br.lines().forEach(w -> {
				wordsToDiscard.put(w, w);
			});			
			fr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}	
	
	private void elabMostFreqWords() {
		Object[] freqs = map.getCurrentMostFreq();
		for (int i = numMostFreqWords-1; i >=0; i--) {
			AbstractMap.SimpleEntry<String, Integer> el = (AbstractMap.SimpleEntry<String, Integer>) freqs[i];
			String key = el.getKey();
			System.out.println(" " + (i+1) + " - " +  key + " " + el.getValue());
		}		
	}

	private void log(String msg) {
		System.out.println("[MASTER]" + msg);
	}

}
