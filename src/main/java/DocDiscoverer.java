import org.bouncycastle.crypto.generators.DHBasicKeyPairGenerator;

import javax.swing.text.Document;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;

public class DocDiscoverer extends BasicTask {

	private File startDir;
	private List<RecursiveTask<WordFreqMap>> forks;
	private int nDocsFound;
	private Flag stopFlag;
	private WordFreqMap map;
	private HashMap<String,String> wordsToDiscard;
	
	public DocDiscoverer(File dir, Flag stopFlag, HashMap<String,String> wordsToDiscard, WordFreqMap map) {
		super("doc-discoverer");
		this.startDir = dir;	
		this.forks = new LinkedList<>();
		this.stopFlag = stopFlag;
		this.map = map;
		this.wordsToDiscard = wordsToDiscard;
	}

	@Override
	public WordFreqMap compute() {
		log("started.");
		nDocsFound = 0;
		if (startDir.isDirectory()) {
			explore(startDir);
			if (stopFlag.isSet()) {
				log("job done - " + nDocsFound + " docs found.");
			} else {
				log("stopped.");
			}
		}
		for (RecursiveTask<WordFreqMap> task : forks) {
			map = task.join();
		}
		return map;
	}
	
	private void explore(File dir) {
		if (!stopFlag.isSet()) {
			for (File f: dir.listFiles()) {
				if (f.isDirectory()) {
					explore(f);
				} else if (f.getName().endsWith(".pdf")) {
					try {
						log("find a new doc: " + f.getName());
						DocLoader task = new DocLoader("" + forks.size(), stopFlag, f, wordsToDiscard, map);
						forks.add(task);
						task.fork();
						nDocsFound++;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
