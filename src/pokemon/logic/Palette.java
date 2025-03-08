package pokemon.logic;

import java.awt.Color;

import pokemon.files.graphics.GraphicResources.ColorBitDepth;

public class Palette {

	public static final int PALETTE_SIZE = 16;
	public static final int COLOR_PER_4BITS_PALETTE = 0x10;
	public static final int COLOR_PER_8BITS_PALETTE = 0x200;
	public static final int MAX_PALETTE_NUMBER = 16;
	
	public static final Palette DEFAULT_PALETTE = new Palette();
	
	private int bitDepth;
	private int colorNumber;
	private int paletteNumber;
	private Color[][] palettes;

	private Palette() {
		this.bitDepth = ColorBitDepth.FOUR_BIT_DEPTH.getBitDepthValue();
		this.colorNumber = COLOR_PER_8BITS_PALETTE;
		this.palettes = new Color[MAX_PALETTE_NUMBER][PALETTE_SIZE];
		for (Color[] row : palettes) {
			for (int i = 0; i < row.length; i++) {
				row[i] = Color.black;
			}
		}
	}
	
	public Palette(int bitDepth, Color[] colors) {
		this();
		this.bitDepth = bitDepth;
		
		if (bitDepth == ColorBitDepth.EIGHT_BIT_DEPTH.getBitDepthValue()) {
			paletteNumber = 1;
		} else {
			// Get number of palettes to create palette array
			paletteNumber = colors.length >> 4;
		}
		
		colorNumber = colors.length;
		for (int palette = 0; palette < MAX_PALETTE_NUMBER; palette++) {
			for (int colorIndex = 0; colorIndex < PALETTE_SIZE; colorIndex++) {
				palettes[palette][colorIndex] = colors[palette * PALETTE_SIZE + colorIndex];
			}
		}

	}

	public Color[][] getPalettes() {
		return this.palettes;
	}
	
	public int getColorPerPalette() {
		return COLOR_PER_4BITS_PALETTE;
	}
	
	public int getPaletteNumber() {
		return paletteNumber;
	}
	
	public void setPaletteNumber(int paletteNumber) {
		this.paletteNumber = paletteNumber;
	}

	public Color getColorInPalette(int paletteNumber, int colorIndex) {
		return this.palettes[paletteNumber][colorIndex];
	}
	
	public Color getColorInPalette(int colorIndex) {
		return getColorInPalette(colorIndex >> 4, colorIndex & 0b1111);
	}
	
	public void setColorInPalette(int paletteNumber, int colorIndex, Color color) {
		this.palettes[paletteNumber][colorIndex] = color;
	}

	/**
	 * Identical to {@link #setColorInPalette(int, int, Color)} but more useful when
	 * you have 8-bit depth
	 * 
	 * @param colorIndex Color index in palette
	 * @param color      Color to set
	 */
	public void setColorInPalette(int colorIndex, Color color) {
		setColorInPalette(colorIndex >> 4, colorIndex & 0b1111, color);
	}

	public int getBitDepth() {
		return bitDepth;
	}

	public void setBitDepth(int bitDepth) {
		this.bitDepth = bitDepth;
		if (bitDepth == ColorBitDepth.EIGHT_BIT_DEPTH.getBitDepthValue()) {
			paletteNumber = 1;
		} else {
			paletteNumber = colorNumber >> 4;
		}
	}
}
