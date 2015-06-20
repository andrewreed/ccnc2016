import java.util.*;
import java.io.*;
import java.text.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.StatUtils;

public class dbCheck {

	public static final int NUM_MOVIES = 104;
	public static final int WINDOW_SIZE = 30;

	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("0.00");

		Movie[] movieArray = new Movie[NUM_MOVIES];

		FileInputStream movieListIS = null;

		try {
			movieListIS = new FileInputStream(args[0]);
		} catch (Exception e) {
			System.out.println("ERROR: Unable to open the fingerprint file.");
			System.exit(0);
		}

		Scanner dataInput = new Scanner(movieListIS);

		int index = 0; 
		while (dataInput.hasNextLine()) {
			movieArray[index] = new Movie(dataInput.nextLine(), WINDOW_SIZE);
			index++;
		}

		FileInputStream comparisonsIS = null;

		try {
			comparisonsIS = new FileInputStream(args[1]);
		} catch (Exception e) {
			System.out.println("ERROR: Unable to open the test input.");
			System.exit(0);
		}

		PearsonsCorrelation correlator = new PearsonsCorrelation();

		Scanner testInput = new Scanner(comparisonsIS);
		
		while (testInput.hasNextLine()) {
			long startTime = System.currentTimeMillis();
			String currentTest = testInput.nextLine();
			String[] tokens = currentTest.split("\\t");
			int movieAIndex = Integer.parseInt(tokens[0]);
			int movieBIndex = Integer.parseInt(tokens[1]);
			Movie movieA = movieArray[movieAIndex];
			Movie movieB = movieArray[movieBIndex];

			if (movieA.getTitle().equals(movieB.getTitle())) {
				continue;
			}

			int allCombos = 0;
			int withinTotalData = 0;
			int withinAllocations = 0;
			int withinCorrel = 0;

			ArrayList<int[]> matchList = new ArrayList<int[]>();

			for (int movieAWindowIndex = 0; movieAWindowIndex < movieA.getNumWindows(); movieAWindowIndex++) {
				Window movieAWindow = movieA.getWindow(movieAWindowIndex);

				double[] movieAKey = movieAWindow.getKey();
				int[] movieASegments = movieAWindow.getSegments();
				
				for (int movieBWindowIndex = 0; movieBWindowIndex < movieB.getNumWindows(); movieBWindowIndex++) {
					Window movieBWindow = movieB.getWindow(movieBWindowIndex);

					double[] movieBKey = movieBWindow.getKey();
					int[] movieBSegments = movieBWindow.getSegments();

					double diffPercentA = (Math.abs(movieAKey[0] - movieBKey[0])) / (movieAKey[0]);
					double diffPercentB = (Math.abs(movieAKey[0] - movieBKey[0])) / (movieBKey[0]);

					allCombos++;

					if ((diffPercentA > 0.01) && (diffPercentB > 0.01)) {
						continue;
					}

					withinTotalData++;

					double diffAlloc1 = Math.abs(movieAKey[1] - movieBKey[1]);
					double diffAlloc2 = Math.abs(movieAKey[2] - movieBKey[2]);
					double diffAlloc3 = Math.abs(movieAKey[3] - movieBKey[3]);
					double diffAlloc4 = Math.abs(movieAKey[4] - movieBKey[4]);
					double diffAlloc5 = Math.abs(movieAKey[5] - movieBKey[5]);

					if ((diffAlloc1 > 0.015) || (diffAlloc2 > 0.015) || (diffAlloc3 > 0.015) || (diffAlloc4 > 0.015) || (diffAlloc5 > 0.015)) {
						continue;
					}

					withinAllocations++;

					ArrayList<Double> diffList = new ArrayList<Double>();

					for (int i = 0; i < 30; i++) {
						diffList.add(Math.abs(movieASegments[movieAWindowIndex + i] - movieBSegments[movieBWindowIndex + i]) / (double)movieASegments[movieAWindowIndex + i]);
					}

					Collections.sort(diffList, Collections.reverseOrder());

					double cutoff = diffList.get(3);

					double[] movieAInliers = new double[26];
					double[] movieBInliers = new double[26];

					int inlierIndex = 0;
					int skipCount = 0;

					for (int i = 0; i < 30; i++) {
						if (((Math.abs(movieASegments[movieAWindowIndex + i] - movieBSegments[movieBWindowIndex + i]) / (double)movieASegments[movieAWindowIndex + i]) < cutoff) || (skipCount == 4)){
							movieAInliers[inlierIndex] = (double)movieASegments[movieAWindowIndex + i];
							movieBInliers[inlierIndex] = (double)movieBSegments[movieBWindowIndex + i];
							inlierIndex++;
						}
						else {
							skipCount++;
						}
					}

					double inlierCorrel = correlator.correlation(movieAInliers, movieBInliers);

					if (inlierCorrel < 0.97) {
						continue;
					}
					
					withinCorrel++;

					matchList.add(new int[]{movieAWindow.getStartIndex(), movieBWindow.getStartIndex()});

					System.out.println("1" + "\t" +
						                 movieA.getTitle() + "\t" +
							               movieA.getBitrate() + "\t" +
							               movieB.getTitle() + "\t" +
							               movieB.getBitrate() + "\t" +
														 (movieAWindow.getStartIndex()+1) + "\t" +
														 (movieBWindow.getStartIndex()+1));
				}
			}

			int[][] matchArray = new int[matchList.size()][];
			matchArray = matchList.toArray(matchArray);

			for (int a = 0; a < matchArray.length-1; a++) {
				for (int b = a+1; b < matchArray.length; b++) {
					int diffA1 = matchArray[b][0] - matchArray[a][0];
					int diffB1 = matchArray[b][1] - matchArray[a][1];

					if ((diffA1 >= 15) && (diffA1 == diffB1)) {
						System.out.println("2" + "\t" +
						                   movieA.getTitle() + "\t" +
							                 movieA.getBitrate() + "\t" +
							                 movieB.getTitle() + "\t" +
							                 movieB.getBitrate() + "\t" +
							                 (matchArray[a][0]+1) + "\t" +
							                 (matchArray[a][1]+1) + "\t" +
							                 diffA1);
					}
				}
			}

			long totalTimeMillis = System.currentTimeMillis() - startTime;
			System.out.println("3" + "\t" +
			                   movieA.getTitle() + "\t" +
			                   movieA.getBitrate() + "\t" +
			                   movieB.getTitle() + "\t" +
			                   movieB.getBitrate() + "\t" +
			                   allCombos + "\t" +
			                   withinTotalData + "\t" +
			                   withinAllocations + "\t" +
			                   withinCorrel + "\t" +
			                   totalTimeMillis);
		}
	}
}
