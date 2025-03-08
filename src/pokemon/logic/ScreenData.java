package pokemon.logic;

import java.awt.Color;

public class ScreenData {

	private int tileNumber;
	private boolean xFlip;
	private boolean yFlip;
	private int paletteNumber;

	/**
	 * Format is YYYYXXNNNNNNNNNN, with
	 * <ul>
	 * <li>Y -> Palette number
	 * <li>X -> bit 0 = X flip, bit 1 = Y flip
	 * <li>N -> Tile number
	 * </ul>
	 */
	public ScreenData(int data) {
		this.tileNumber = data & 0b1111111111;
		this.xFlip = ((data >> 10) & 0b1) == 1;
		this.yFlip = ((data >> 11) & 0b1) == 1;
		this.paletteNumber = (data >> 12) & 0b1111;

	}

	public Color[] processTile(Tile[] tiles, Palette palette) {
		Color[] colors = new Color[Tile.TILE_SIZE * Tile.TILE_SIZE];
		if (tileNumber >= tiles.length) {
			for (int i = 0; i < colors.length; i++) {
				colors[i] = Color.black;
			}
		} else {
			Tile tile = tiles[tileNumber];
			int[][] tileData = tile.getTileData();

			// Process tile flips
			int[] processedData = new int[Tile.TILE_SIZE * Tile.TILE_SIZE];
			for (int i = 0; i < Tile.TILE_SIZE; i++) {
				for (int j = 0; j < Tile.TILE_SIZE; j++) {
					int xIndex = xFlip ? Tile.TILE_SIZE - i - 1 : i;
					int yIndex = yFlip ? Tile.TILE_SIZE - j - 1 : j;
					processedData[i * Tile.TILE_SIZE + j] = tileData[yIndex][xIndex];
				}
			}

			// Get colors
			Color[] selectedPalette = palette.getPalettes()[paletteNumber];
			for (int i = 0; i < processedData.length; i++) {
				colors[i] = selectedPalette[processedData[i]];
			}
		}
		return colors;
	}

}
