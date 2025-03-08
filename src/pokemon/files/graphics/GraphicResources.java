package pokemon.files.graphics;

import java.awt.Color;


/**
 * Here to store bit depth and color related functions
 */
public class GraphicResources {

	// No instantiation
	private GraphicResources() {
	}

	public enum ColorBitDepth {
		FOUR_BIT_DEPTH(3), EIGHT_BIT_DEPTH(4);

		private int bitDepthValue;

		private ColorBitDepth(int bitDepthBalue) {
			this.bitDepthValue = bitDepthBalue;
		}

		public int getBitDepthValue() {
			return bitDepthValue;
		}

		public static ColorBitDepth fromBitDepth(int bitDepthValue) {
			return bitDepthValue == 3 ? FOUR_BIT_DEPTH : EIGHT_BIT_DEPTH;
		}
	}
	
	public static int getBGR555FromRGB(Color color) {
		int r = color.getRed() >> 3;
		int g = (color.getGreen() >> 3) << 5;
		int b = (color.getBlue() >> 3) << 10;

		return r + g + b;
	}
	
	public static Color getRGBFromBGR555(int bgr) {
		int r = (bgr & 0b11111) << 3;
		int g = ((bgr >> 5) & 0b11111) << 3;
		int b = ((bgr >> 10) & 0b11111) << 3;

		int rError = r >> 5;
		int gError = g >> 5;
		int bError = b >> 5;

		return new Color(r + rError, g + gError, b + bError);
	}

}