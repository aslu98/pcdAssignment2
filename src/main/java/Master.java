import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Master extends BasicTask {

	private File configFile;
	private File dir;
	private int numMostFreqWords;
	
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	private ExecutorService executor;
	
	private Flag stopFlag;

	private View view;

	/* performance tuning params */
	private static final int docFilesBufferSize = 100;
	private static final int chunksBufferSize = 100;

	public Master(File configFile, File dir, int numMostFreqWords, Flag stopFlag, View view) {
		super("master");
		this.configFile = configFile;
		this.dir = dir;
		this.numMostFreqWords = numMostFreqWords;
		this.view = view;
		this.stopFlag = stopFlag;
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
	}

	public void compute() {
		log("started.");
		try {
			int nDocLoaderAgents = Runtime.getRuntime().availableProcessors();
			int nTextAnalyzerAgents = Runtime.getRuntime().availableProcessors();
			long t0 = System.currentTimeMillis();

			map = new WordFreqMap(numMostFreqWords);

			Flag done = new Flag();
			Viewer viewer = new Viewer(map,view,done);
			viewer.start();

			loadWordsToDiscard(configFile);

			/* spawn discoverer */
			
			BoundedBuffer<File> docFiles = new BoundedBuffer<File>(docFilesBufferSize);
			Future discovererResult = this.executor.submit(new DocDiscoverer(dir, docFiles, stopFlag));

			/* spawn doc loaders */
			
			BoundedBuffer<String> chunks = new BoundedBuffer<String>(chunksBufferSize);
			List<Future> loadersResults = new LinkedList<>();
			for (int i = 0; i < nDocLoaderAgents; i++) {
				loadersResults.add(this.executor.submit(new DocLoader("" + i, docFiles, chunks, stopFlag)));
			}
			
			/* spawn doc analyzers */

			List<Future> analyzerResults = new LinkedList<>();
			for (int i = 0; i < nTextAnalyzerAgents; i++) {
				analyzerResults.add(this.executor.submit(new TextAnalyzer("" + i, wordsToDiscard, chunks, map, stopFlag)));
			}
			
			/* wait for loaders to complete */
			for (Future loader: loadersResults){
				loader.get();
			}
			log("loaders done.");

			/* no more chunks will be added */
			chunks.close();

			/* wait for analyzers to complete */
			for (Future analyzer: analyzerResults){
				analyzer.get();
			}
			log("analyzers done.");
						
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

}
