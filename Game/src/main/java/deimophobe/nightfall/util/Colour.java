package deimophobe.nightfall.util;

/**
 * Created by Deimophobe on 25/03/18.
 */
@Deprecated
public class Colour {
	private final double red;
	private final double green;
	private final double blue;
	
	public Colour(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		
		checkColours();
	}
	
	public static Colour fromRGB(int red, int green, int blue) {
		return new Colour((double)red/256, (double)green/256, (double)blue/256);
	}
	
	public double getRed() {
		return red;
	}
	public double getGreen() {
		return green;
	}
	public double getBlue() {
		return blue;
	}
	
	private void checkColours() {
		checkColour(red, "red");
		checkColour(green, "green");
		checkColour(blue, "blue");
	}
	
	
	private static void checkColour(double value, String colourName) {
		if (value < 0 || value > 1) throw new IllegalArgumentException("Colour " + colourName + " should have value between 0 and 1, got " + value);
	}
}
