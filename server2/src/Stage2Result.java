
public class Stage2Result implements Comparable<Stage2Result> {
	protected String clientQueryTS;
	protected String guessedTitle;
	protected int guessedWindoStartIndex;
	protected double percentBandwidthDiff;
	protected double pCorrel;
	public Stage2Result(String ts, String title, int index, double per, double p) {
		clientQueryTS=ts;
		guessedTitle=title;
		guessedWindoStartIndex=index;
		percentBandwidthDiff = per;
		pCorrel=p;
	}
	
	public String getTitle(){
		return guessedTitle;
		
	}
	@Override
	public int compareTo(Stage2Result that) {
		//High decimal number wins in pearson's correlation
		//Java PQ is a min queue!
		if(this.pCorrel > that.pCorrel) return -1;
		else if(this.pCorrel < that.pCorrel) return 1;
		else return 0;
	}

}
