
public class Stage1Result implements Comparable<Stage1Result> {
	protected double distance;
	protected Window window;
	
	public Stage1Result(double d, Window compareWindow) {
		// TODO Auto-generated constructor stub
		this.distance = d;
		this.window = compareWindow;
	}
	
	public double getDistance(){ 
		return distance;
		}
	
	public Window getWindow(){ 
		return window;
		}
	
	@Override
	public int compareTo(Stage1Result that) {
		//Low distance wins in pearson's correlation
		//Java PQ is a min queue!
		if(this.distance < that.distance) return -1;
		else if(this.distance > that.distance) return 1;
		else return 0;
	}

}
