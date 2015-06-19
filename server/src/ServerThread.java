import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.StatUtils;
import net.sf.javaml.core.kdtree.KDTree;

public class ServerThread implements Runnable {

	private Socket clientSocket;
	private KDTree windowDB;

	public static final int WINDOW_SIZE = 30;

	public ServerThread(Socket clientSocket, KDTree windowDB) {
		this.clientSocket = clientSocket;
		this.windowDB = windowDB;
	}

	@Override
	public void run() {
		PearsonsCorrelation correlator = new PearsonsCorrelation();

		String ipAddr = clientSocket.getInetAddress().toString();

		System.out.println(ipAddr + "\tconnected");

		PrintWriter out = null;
		Scanner in = null;

		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = new Scanner(clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println(ipAddr + "\treader/writer failed"); 
			return; 
		} 

		while (in.hasNextLine()) {
			String inputLine = in.nextLine();
			String[] tokens = inputLine.split("\\t");

			if (tokens[0].equals("complete")) {
				out.print("\f");
				System.out.println(ipAddr + "\tcomplete");
				break;
			}

			String macAddr = tokens[0];
			String startTime = tokens[1];

			String[] readings = tokens[2].split(",");
			int[] sums = new int[WINDOW_SIZE];

			for (int i = 0; i < WINDOW_SIZE; i++) {
				sums[i] = Integer.parseInt(readings[i]);
			}

			short dummyBitrate = 0;
			Window currentWindow = new Window(startTime, dummyBitrate, 0, sums);
			
			double[] key = currentWindow.getKey();

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

			int[] currentSegments = currentWindow.getSegments();

			for (int i = 0; i < shortList.length; i++) {
				Window compareWindow = (Window)shortList[i];
				int compareStart = compareWindow.getStartIndex();
				int[] compareSegments = compareWindow.getSegments();

				ArrayList<Double> diffList = new ArrayList<Double>();

				for (int y = 0; y < currentSegments.length; y++) {
					diffList.add(Math.abs(currentSegments[y] - compareSegments[compareStart + y]) / (double)currentSegments[y]);
				}

				Collections.sort(diffList, Collections.reverseOrder());

				double cutoff = diffList.get(3);

				double[] currentInliers = new double[26];
				double[] compareInliers = new double[26];

				int inlierIndex = 0;
				int skipCount = 0;

				for (int y = 0; y < currentSegments.length; y++) {
					if (((Math.abs(currentSegments[y] - compareSegments[compareStart + y]) / (double)currentSegments[y]) < cutoff) || (skipCount == 4)){
						currentInliers[inlierIndex] = (double)currentSegments[y];
						compareInliers[inlierIndex] = (double)compareSegments[compareStart + y];
						inlierIndex++;
					}
					else {
						skipCount++;
					}
				}

				double inlierCorrel = correlator.correlation(currentInliers, compareInliers);

				if (inlierCorrel < 0.97) {
					continue;
				}

				out.println(macAddr + "\t" +
				            currentWindow.getTitle() + "\t" + 
				            compareWindow.getTitle() + "\t" + 
				            compareWindow.getStartIndex() + "\t" +
				            compareWindow.getKey()[0] / key[0] + "\t" +
				            inlierCorrel);
			}
		}
		try {
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) { 
			System.err.println(ipAddr + "\tclean-up failed"); 
		} 
		System.out.println(ipAddr + "\tdisconnected");
	}
}
