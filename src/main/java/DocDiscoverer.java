import java.io.File;
import java.util.concurrent.Callable;

public class DocDiscoverer extends BasicTask implements Callable<Boolean> {

	private File startDir;
	private BoundedBuffer<File> docFiles;
	private int nDocsFound;
	private Flag stopFlag;
	
	public DocDiscoverer(File dir, BoundedBuffer<File> docFiles, Flag stopFlag) {
		super("doc-discoverer");
		this.startDir = dir;	
		this.docFiles = docFiles;
		this.stopFlag = stopFlag;
	}

	@Override
	public Boolean call() {
		log("started.");
		nDocsFound = 0;
		if (startDir.isDirectory()) {
			explore(startDir);
			docFiles.close();
			if (stopFlag.isSet()) {
				log("job done - " + nDocsFound + " docs found.");
			} else {
				log("stopped.");
			}
		}
		return true;
	}
	
	private void explore(File dir) {
		if (!stopFlag.isSet()) {
			for (File f: dir.listFiles()) {
				if (f.isDirectory()) {
					explore(f);
				} else if (f.getName().endsWith(".pdf")) {
					try {
						log("find a new doc: " + f.getName());
						docFiles.put(f);
						nDocsFound++;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
