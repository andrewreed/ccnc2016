public class Movie {

	private String title;
  private short bitrate;
	private Window[] windows;

	// Constructor
	public Movie(String titleBitrateAndFingerprint, int windowSize) {
		String[] titleBitrateAndFingerprintArray = titleBitrateAndFingerprint.split("\\t");

		title = titleBitrateAndFingerprintArray[0];

		bitrate = Short.parseShort(titleBitrateAndFingerprintArray[1]);

		String[] fingerprintAsStringArray = titleBitrateAndFingerprintArray[2].split(",");
		int[] fingerprint = new int[fingerprintAsStringArray.length];
		for (int i = 0; i < fingerprintAsStringArray.length; i++) {
			fingerprint[i] = Integer.parseInt(fingerprintAsStringArray[i]);
		}

		int numWindows = fingerprint.length - windowSize + 1;
		windows = new Window[numWindows];
		for (int i = 0; i < numWindows; i++) {
			windows[i] = new Window(title, bitrate, i, fingerprint);
		}
	}

	public String getTitle() {
		return title;
	}

	public short getBitrate() {
		return bitrate;
	}

	public int getNumWindows() {
		return windows.length;
	}

	public Window getWindow(int index) {
		return windows[index];
	}
}
