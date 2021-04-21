package part1.jpf;

import java.util.HashMap;

public class TextAnalyzerTask extends BasicTask {

	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	private String chunk;

	public TextAnalyzerTask(String id, String chunk, HashMap<String,String> wordsToDiscard, WordFreqMap map)  {
		super("text-analyzer-" + id);
		this.wordsToDiscard = wordsToDiscard;
		this.map = map;
		this.chunk = chunk;
	}
	
	public void compute() {
		try {		    
		    String del = "[\\x{201D}\\x{201C}\\s'\", ?.@;:!-]+";
				String[] words = chunk.split(del);
				for (String w: words) {
					String w1 = w.trim().toLowerCase();
					if (!w1.equals("") && !wordsToDiscard.containsKey(w1)) {
						map.add(w1);
					}
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
