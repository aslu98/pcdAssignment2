package part1.jpf;

import java.util.*;

public class WordFreqMap {

	private HashMap<String, Integer> freqs;

	public WordFreqMap() {
		freqs = new HashMap<>();
	}

	public synchronized void add(String word) {
		Integer nf = freqs.get(word);
		if (nf == null) {
			freqs.put(word, 1);
		} else {
			freqs.put(word, nf + 1);
		}
	}

	public synchronized HashMap<String, Integer> getWords(){
		return freqs;
	}


}
