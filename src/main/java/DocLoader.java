import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;

public class DocLoader extends BasicTask{

	private PDFTextStripper stripper;
	private Flag stopFlag;
	private WordFreqMap map;
	private HashMap<String,String> wordsToDiscard;
	private List<RecursiveTask<WordFreqMap>> forks;
	private File doc;
	
	public DocLoader(String id, Flag stopFlag, File f, HashMap<String,String> wordsToDiscard, WordFreqMap map) throws Exception  {
		super("doc-loader-" + id);
		this.stopFlag = stopFlag;
        stripper = new PDFTextStripper();
		this.map = map;
		this.wordsToDiscard = wordsToDiscard;
		this.doc = f;
		this.forks = new LinkedList<>();
	}

	@Override
	public WordFreqMap compute(){
		log("started");
		if (!stopFlag.isSet()) {
			log("got a doc to load: " + doc.getName());
			try {
				loadDoc(doc);
			} catch (Exception ex) {
			}
		} else {
			log("stopped");
		}
		log("done.");
		for (RecursiveTask<WordFreqMap> task : forks) {
			map = task.join();
		}
		return map;
	}
	
	private void loadDoc(File doc) throws Exception {
        PDDocument document = PDDocument.load(doc);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent())
        {
            throw new IOException("You do not have permission to extract text");
        }

        int nPages = document.getNumberOfPages();
        log("Chunks to be processed: " + nPages);
        for (int i = 0; i < nPages; i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String chunk =  stripper.getText(document);
			TextAnalyzer task = new TextAnalyzer("" + forks.size(), stopFlag, chunk, wordsToDiscard, map);
			forks.add(task);
			task.fork();
        }
  	}
}
