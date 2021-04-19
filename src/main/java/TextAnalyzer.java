import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class TextAnalyzer extends BasicTask {

	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	private Flag stopFlag;
	private String chunk;

	public TextAnalyzer(String id, Flag stopFlag, String chunk, HashMap<String,String> wordsToDiscard, WordFreqMap map)  {
		super("text-analyzer-" + id);
		this.wordsToDiscard = wordsToDiscard;
		this.map = map;
		this.stopFlag = stopFlag;
		this.chunk = chunk;
	}
	
	public WordFreqMap compute() {
		try {		    
		    String del = "[\\x{201D}\\x{201C}\\s'\", ?.@;:!-]+";
			if (!stopFlag.isSet()) {
				String[] words = chunk.split(del);
				for (String w: words) {
					String w1 = w.trim().toLowerCase();
					if (!w1.equals("") && !wordsToDiscard.containsKey(w1)) {
						map.add(w1);
					}
				}
			} else {
				log("stopped.");
			}
			log("done.");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return map;
	}
}
