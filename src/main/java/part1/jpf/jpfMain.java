package part1.jpf;
import java.util.concurrent.ForkJoinPool;

public class jpfMain {
	public static void main(String[] args) {
		try {
			final ForkJoinPool forkJoinPool = new ForkJoinPool();
			int numMostFreqWords = 10;
			File configFile = new FileMockImpl("");
			File dir = ModelCheckingData.getInstance().genMockDirData();
			Master master = new Master(configFile, dir, numMostFreqWords);

			forkJoinPool.submit(master);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
