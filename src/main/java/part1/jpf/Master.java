package part1.jpf;

import gov.nasa.jpf.vm.Verify;

import java.util.*;

public class Master extends BasicTask {

	private final File configFile;
	private final File dir;
	private final int numMostFreqWords;
	
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;

	public Master(File configFile, File dir, int numMostFreqWords) {
		super("Master");
		this.configFile = configFile;
		this.dir = dir;
		this.numMostFreqWords = numMostFreqWords;
	}

	public void compute() {
		try {
			log("started ");
			long t0 = System.currentTimeMillis();

			map = new WordFreqMap();
			loadWordsToDiscard(configFile);

			DocDiscovererTask masterTask = new DocDiscovererTask(dir, wordsToDiscard, map);
			masterTask.fork();
			masterTask.join();
			
			elabMostFreqWords();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	private void loadWordsToDiscard(File configFile) {
		wordsToDiscard = new HashMap<String,String>();
	}	
	
	private void elabMostFreqWords() {
		Verify.beginAtomic();
		Set<Map.Entry<String, Integer>> set = map.getWords().entrySet();

		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
		list.addAll(set);

		list.sort((Map.Entry<String,Integer> e1, Map.Entry<String,Integer> e2) -> {
			return Integer.compare(e2.getValue().intValue(), e1.getValue().intValue());
		});

		for (int i = 0; i < numMostFreqWords && i < list.size(); i++) {
			String key = list.get(i).getKey();
			System.out.println(" " + (i+1) + " - " +  key + " " + list.get(i).getValue());
		}

		ModelCheckingData.getInstance().checkResults(list);

		Verify.endAtomic();
	}
}
