import java.text.DecimalFormat;
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
	private double pearsonThres;
	private double lowMultFactor;
	private double highDThreshold;
	private double highMultFactor;
	private String movieTitle = "Not_given";
	private boolean clientConfigured;

	public static final int WINDOW_SIZE = 30;

	public ServerThread(Socket clientSocket, KDTree windowDB) {
		//BHK This is the orginal constructor used by the Netflid
		this.clientSocket = clientSocket;
		this.windowDB = windowDB;
		this.pearsonThres=0.97;
		this.lowMultFactor=0.98;
		this.highDThreshold=0.015;
		this.highMultFactor=1.02;
		
	}

	public ServerThread(Socket clientSocket, KDTree windowDB, double pearsonThres, double lowMultFactor, double highDThreshold, double highMultFactor) {
		this.clientSocket = clientSocket;
		this.windowDB = windowDB;
		this.pearsonThres=pearsonThres;
		this.lowMultFactor=lowMultFactor;
		this.highDThreshold=highDThreshold;
		this.highMultFactor=highMultFactor;
	}
	
	@Override
	public void run() {
		PearsonsCorrelation correlator = new PearsonsCorrelation();

		String ipAddr = clientSocket.getInetAddress().toString();

		System.out.println(ipAddr + "\tconnected");

		PrintWriter out = null;
		PrintWriter stage1 = null;
		PrintWriter stage2 = null;
		Scanner in = null;

		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = new Scanner(clientSocket.getInputStream());
			//stage1 = new PrintWriter("stage1Results_"+optionsToString()+".csv");
			//stage2 = new PrintWriter("stage2Results_"+optionsToString()+".csv");
			
		} catch (IOException e) {
			System.err.println(ipAddr + "\treader/writer failed"); 
			return; 
		} 
		int lineCounter = 0;
		while (in.hasNextLine()) {
			lineCounter++;
			//System.out.println("This is iteration "+Integer.toString(lineCounter));
			String inputLine = in.nextLine();
			String[] tokens = inputLine.split("\\t");
			if (tokens[0].equals("complete")) {
				out.print("\f");
				lineCounter=0;
				clientConfigured = false;
				if(stage1 != null) stage1.println("===========================================================");
				if(stage2 != null) stage2.println("===========================================================");
				System.out.println(ipAddr + "\tcomplete");
				break;
			}
			
			
			String macAddr = tokens[0];
			String startTime = tokens[1];
			String[] readings = tokens[2].split(",");
			if(tokens.length==4 && !clientConfigured){
				String[] clientOptions = tokens[3].split(",");//TODO update client 
				setParameters(clientOptions);
				clientConfigured=true;
				try {
					stage1 = new PrintWriter("stage1Results_"+optionsToString()+".csv");
					stage2 = new PrintWriter("stage2Results_"+optionsToString()+".csv");
					
				} catch (IOException e) {
					System.err.println(ipAddr + "\treader/writer failed"); 
					return; 
				}
				
			}
			int[] sums = new int[WINDOW_SIZE];

			for (int i = 0; i < WINDOW_SIZE; i++) {
				sums[i] = Integer.parseInt(readings[i]);
			}

			short dummyBitrate = 0;
			//currentWindow is the query from the client
			Window currentWindow = new Window(startTime, dummyBitrate, 0, sums);
			
			double[] key = currentWindow.getKey();
			
			//BHK Stage One!
			//BHK Add output redirection/file writing
			//BHK Initialize factors in constructor
			double[] lowerKey = new double[]{key[0] * this.lowMultFactor,
			                                 key[1] - this.highDThreshold,
			                                 key[2] - this.highDThreshold,
			                                 key[3] - this.highDThreshold,
			                                 key[4] - this.highDThreshold,
			                                 key[5] - this.highDThreshold};

			double[] upperKey = new double[]{key[0] * this.highMultFactor,
			                                 key[1] + this.highDThreshold,
			                                 key[2] + this.highDThreshold,
			                                 key[3] + this.highDThreshold,
			                                 key[4] + this.highDThreshold,
			                                 key[5] + this.highDThreshold};
			
			Object[] shortList = windowDB.range(lowerKey, upperKey);

			int[] currentSegments = currentWindow.getSegments();
			PriorityQueue<Stage1Result> q1 = new PriorityQueue<Stage1Result>(50);
			PriorityQueue<Stage2Result> q2 = new PriorityQueue<Stage2Result>(30);
			
			for (int i = 0; i < shortList.length; i++) {
				Window compareWindow = (Window)shortList[i];
				int compareStart = compareWindow.getStartIndex();
				int[] compareSegments = compareWindow.getSegments();

				ArrayList<Double> diffList = new ArrayList<Double>();

				for (int y = 0; y < currentSegments.length; y++) {
					diffList.add(Math.abs(currentSegments[y] - compareSegments[compareStart + y]) / (double)currentSegments[y]);
				}

				Collections.sort(diffList, Collections.reverseOrder());
				
				//BHK Stage Two ???
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
				q1.add( new Stage1Result(arraySum(diffList.subList(4, diffList.size())),compareWindow) );
				/*double[] currentSnapSamples = new double[20];
				for (int y = 0; y < 20; y++) {
					currentSnapSamples[y] = ((3.0*currentInliers[y]) + 
								                  (-7.0*currentInliers[y+1]) + 
								                   (1.0*currentInliers[y+2]) + 
								                   (6.0*currentInliers[y+3]) + 
								                   (1.0*currentInliers[y+4]) + 
								                  (-7.0*currentInliers[y+5]) + 
								                   (3.0*currentInliers[y+6])) / 11.0;
				}

				double[] compareSnapSamples = new double[20];
				for (int y = 0; y < 20; y++) {
					compareSnapSamples[y] = ((3.0*compareInliers[y]) + 
								                  (-7.0*compareInliers[y+1]) + 
								                   (1.0*compareInliers[y+2]) + 
								                   (6.0*compareInliers[y+3]) + 
								                   (1.0*compareInliers[y+4]) + 
								                  (-7.0*compareInliers[y+5]) + 
								                   (3.0*compareInliers[y+6])) / 11.0;
				}

				double snapCorrel = correlator.correlation(currentSnapSamples, compareSnapSamples);*/
				
				double snapCorrel = correlator.correlation(currentInliers, compareInliers);
				//BHK Stage Two Pearson
				if (snapCorrel < this.pearsonThres) {
					continue;
				}
				
				
				out.println(macAddr + "\t" +
				            currentWindow.getTitle() + "\t" + 
				            compareWindow.getTitle() + "\t" + 
				            compareWindow.getStartIndex() + "\t" +
				            compareWindow.getKey()[0] / key[0] + "\t" +
				            snapCorrel);
				q2.add(new Stage2Result( currentWindow.getTitle(), compareWindow.getTitle(), compareWindow.getStartIndex(),
						compareWindow.getKey()[0] / key[0], snapCorrel));
			}
			printStageOneResults(lineCounter,q1,stage1);
			printStageTwoResults(lineCounter, q2, stage2);
			
		}
		try {
			out.close();
			in.close();
			clientSocket.close();
			stage1.close();
			stage2.close();
		} catch (Exception e) { 
			System.err.println(ipAddr + "\tclean-up failed"); 
		} 
		System.out.println(ipAddr + "\tdisconnected");
	}

	private String optionsToString() {
		DecimalFormat form = new DecimalFormat("#.###");
		return this.pearsonThres+"_"+(form.format(1.0-this.lowMultFactor))+"_"+this.highDThreshold+"_"+this.movieTitle;
	}

	private void setParameters(String[] clientOptions) {
		// BHK This method allows the client to decide the 
		// What parameters should be used in Stage 1 and stage 2
		this.pearsonThres=Double.parseDouble(clientOptions[0]);
		this.lowMultFactor=Double.parseDouble(clientOptions[1]);
		this.highDThreshold=Double.parseDouble(clientOptions[3]);
		this.highMultFactor=Double.parseDouble(clientOptions[2]);
		this.movieTitle=clientOptions[4];
	}

	private double arraySum(List<Double> subList) {
		Iterator<Double> i = subList.iterator();
		double ans = 0.0;
		while(i.hasNext()) ans+=i.next();
		return ans;
	}

	private void printStageOneResults(int lineNum, PriorityQueue<Stage1Result> q1, PrintWriter stage1) {
		//This method prints/writes the results from varying the stage 1 filter
        //Rank ordered by distance		
		if(stage1 ==null) return;
		stage1.print(lineNum);
		stage1.print(",");
		stage1.print(q1.size());
		if(!q1.isEmpty()) stage1.print(",");
		while(!q1.isEmpty()){
			Stage1Result result = q1.poll();
			stage1.print(result.getWindow().getTitle());
			if(!q1.isEmpty())stage1.print(",");
		}
		stage1.println();
	}
	
	private void printStageTwoResults(int lineNum, PriorityQueue<Stage2Result> q2,  PrintWriter stage2) {
		//This method prints/writes the results from varying the stage 1 filter
        //Rank ordered by distance
		if(stage2 == null) return;
		stage2.print(lineNum);
		stage2.print(",");
		stage2.print(q2.size());
		if(!q2.isEmpty()) stage2.print(",");
		while(!q2.isEmpty()){
			Stage2Result result = q2.poll();
			stage2.print(result.getTitle());
			if(!q2.isEmpty())stage2.print(",");
		}
		stage2.println();
	}
		
}

