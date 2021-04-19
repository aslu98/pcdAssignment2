import java.util.HashMap;
import java.util.concurrent.Callable;

public class TextAnalyzer extends BasicTask implements Callable<WordFreqMap> {

	private BoundedBuffer<String> chunks;
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	private Flag stopFlag;

	public TextAnalyzer(String id, HashMap<String,String> wordsToDiscard, BoundedBuffer<String> chunks, WordFreqMap map, Flag stopFlag)  {
		super("text-analyzer-" + id);
		this.chunks = chunks;
		this.wordsToDiscard = wordsToDiscard;
		this.map = map;
		this.stopFlag = stopFlag;
	}
	
	public WordFreqMap call() {
		log("started.");
		try {		    
		    String del = "[\\x{201D}\\x{201C}\\s'\", ?.@;:!-]+";
	
		    long nChunks = 0;
		    boolean noMoreWork = false;
		    
			while (!noMoreWork) {
				try {
					String chunk = chunks.get();	
					if (!stopFlag.isSet()) {
						nChunks++;
						String[] words = chunk.split(del);
						for (String w: words) {
							String w1 = w.trim().toLowerCase();
							if (!w1.equals("") && !wordsToDiscard.containsKey(w1)) {
								map.add(w1);
							}
						}
						log("chunk " + nChunks + " processed.");
					} else {
						log("stopped.");
					}
				} catch (ClosedException ex) {
					log("no more work to do.");
					noMoreWork = true;
				}
			}
			log("done.");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}
}
