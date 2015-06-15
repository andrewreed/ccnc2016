public class Window {

	private String title;
  private short bitrate;
	private int startIndex;
	private int[] segments;

	// Constructor
	public Window(String title, short bitrate, int startIndex, int[] segments) {
		this.title = title;
		this.bitrate = bitrate;
		this.startIndex = startIndex;
		this.segments = segments;
	}

	public String getTitle() {
		return title;
	}

	public short getBitrate() {
		return bitrate;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public double[] getKey() {
		double[] key = new double[6];

		key[1] = 0.0;
		key[2] = 0.0;
		key[3] = 0.0;
		key[4] = 0.0;
		key[5] = 0.0;

		for (int i = 0; i < 6; i++) {
			key[1] += segments[startIndex +  0 + i];
			key[2] += segments[startIndex +  6 + i];
			key[3] += segments[startIndex + 12 + i];
			key[4] += segments[startIndex + 18 + i];
			key[5] += segments[startIndex + 24 + i];
		}

		key[0] = key[1] + key[2] + key[3] + key[4] + key[5];

		key[1] = key[1] / key[0];
		key[2] = key[2] / key[0];
		key[3] = key[3] / key[0];
		key[4] = key[4] / key[0];
		key[5] = key[5] / key[0];

		return key;
	}

	public int[] getSegments() {
		return segments;
	}
}
