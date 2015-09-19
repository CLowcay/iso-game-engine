package isogame;

public class View {
	public CameraAngle angle;

	// These are in iso space coordinates.  Please refer to coordinates.txt for
	// more information.
	public double x;
	public double y;

	// These are in screen coordinates
	public double viewportW;
	public double viewportH;

	public void setViewport(int w, int h) {
		viewportW = w;
		viewportH = h;
	}
}

