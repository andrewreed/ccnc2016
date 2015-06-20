import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.StatUtils;
import net.sf.javaml.core.kdtree.KDTree;

public class Netflid {

	public static final int WINDOW_SIZE = 30;

	public static void main(String[] args) {
		KDTree windowDB = new KDTree(6);

		FileInputStream movieListIS = null;

		try {
			movieListIS = new FileInputStream(args[0]);
		} catch (Exception e) {
			System.out.println("ERROR: Unable to open the fingerprint file.");
			System.exit(0);
		}

		Scanner dataInput = new Scanner(movieListIS);

		while (dataInput.hasNextLine()) {
			Movie currentMovie = new Movie(dataInput.nextLine(), WINDOW_SIZE);

			for (int i = 0; i < currentMovie.getNumWindows(); i++) {
				Window currentWindow = currentMovie.getWindow(i);
				windowDB.insert(currentWindow.getKey(), currentWindow);
			}
		}

		dataInput.close();

		ServerSocket serverSocket = null; 

		try { 
			if(args.length == 2) serverSocket = new ServerSocket(Integer.parseInt(args[1]));
			else serverSocket = new ServerSocket(10007); 
		} catch (IOException e) { 
			if(args.length == 2) System.err.println("Could not listen on port: "+args[1]);
			else System.err.println("Could not listen on port: 10007."); 
			System.exit(1); 
		} 

		System.out.println("Server started");

		while (true) {
			Socket clientSocket = null; 

			try { 
				clientSocket = serverSocket.accept(); 

				ServerThread serverThread = new ServerThread(clientSocket, windowDB);
				//TODO Change constructor to hangle args
				Thread serverThread1 = new Thread(serverThread, "server");

				serverThread1.start();
			} catch (IOException e) { 
				System.err.println("Accept failed."); 
			}
		}
		/*try {
			serverSocket.close();	
		} catch (IOException e) { 
			System.err.println("Clean-up failed."); 
			System.exit(1); 
		} */
	}
}
