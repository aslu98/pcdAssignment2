import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class DocLoader extends BasicTask implements Callable<PDFTextStripper> {

	private BoundedBuffer<File> docFiles;
	private BoundedBuffer<String> chunks;
	private PDFTextStripper stripper;
	private Flag stopFlag;
	
	public DocLoader(String id, BoundedBuffer<File> docFiles, BoundedBuffer<String> chunks, Flag stopFlag) throws Exception  {
		super("doc-loader-" + id);
		this.stopFlag = stopFlag;
		this.docFiles = docFiles;
		this.chunks = chunks;
        stripper = new PDFTextStripper();
	}

	@Override
	public PDFTextStripper call(){
		log("started");
		int nJobs = 0;
		boolean noMoreDocs = false;
		while (!noMoreDocs) {
			try {
				File doc = docFiles.get();
				if (!stopFlag.isSet()) {
					nJobs++;
					log("got a doc to load: " + doc.getName() + " - job: " + nJobs);
					try {
						loadDoc(doc);
					} catch (Exception ex) {
					}
				} else {
					log("stopped");
				}
			} catch (ClosedException ex) {
				log("no more docs.");
				noMoreDocs = true;
			}
		}
		log("done.");
		return stripper;
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
			chunks.put(chunk);	
			log("chunk added (" + i + ")");
        }
  	}
}
