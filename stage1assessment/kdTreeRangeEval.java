import java.util.*;
import java.io.*;
import java.text.*;
import net.sf.javaml.core.kdtree.KDTree;

public class kdTreeRangeEval {

	public static final int NUM_WINDOWS = 584776;
	public static final int WINDOW_SIZE = 30;

	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("0.00");

		Window[] windowArray = new Window[NUM_WINDOWS];
		KDTree windowDB = new KDTree(6);

		FileInputStream movieListIS = null;

		try {
			movieListIS = new FileInputStream(args[0]);
		} catch (Exception e) {
			System.out.println("ERROR: Unable to open the fingerprint file.");
			System.exit(0);
		}

		Scanner dataInput = new Scanner(movieListIS);

		int count = 0; 
		while (dataInput.hasNextLine()) {
			Movie currentMovie = new Movie(dataInput.nextLine(), WINDOW_SIZE);

			for (int i = 0; i < currentMovie.getNumWindows(); i++) {
				Window currentWindow = currentMovie.getWindow(i);
				windowDB.insert(currentWindow.getKey(), currentWindow);

				windowArray[count] = currentWindow;
				count++;
			}

		}

		double[] results = new double[NUM_WINDOWS];
		for (int i = 0; i < count; i++) {
			Window windowToCheck = windowArray[i];
			double[] key = windowToCheck.getKey();

			double[] lowerKey = new double[]{key[0] * 0.98,
			                                 key[1] - 0.015,
			                                 key[2] - 0.015,
			                                 key[3] - 0.015,
			                                 key[4] - 0.015,
			                                 key[5] - 0.015};

			double[] upperKey = new double[]{key[0] * 1.02,
			                                 key[1] + 0.015,
			                                 key[2] + 0.015,
			                                 key[3] + 0.015,
			                                 key[4] + 0.015,
			                                 key[5] + 0.015};

			Object[] shortList = windowDB.range(lowerKey, upperKey);
			System.out.println(shortList.length);
		}
	}
}
