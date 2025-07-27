package pokemon.logic;

import pokemon.files.graphics.GraphicResources.ColorBitDepth;

public class Tiles {

	public static final Tiles DEFAULT_TILES = new Tiles(new Tile[0], 0, 0);

	private int tileX;
	private int tileY;
	private Tile[][] tiles;
	private ColorBitDepth colorBitDepth;

	public Tiles(Tile[] tiles, int tileX, int tileY) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.tiles = resize(tiles, tileX, tileY, tileX, tileY);
	}

	public void resizeXTiles(int tileX) {
		tiles = resize(tiles, tiles[0].length, tileY, tileX, tileY);
	}

	public void resizeYTiles(int tileY) {
		tiles = resize(tiles, tileX, tiles.length, tileX, tileY);
	}

	public Tile getTile(int tileX, int tileY) {
		if (tileX >= this.tileX || tileY >= this.tileY) {
			return Tile.DEFAULT_TILE;
		}

		return tiles[tileY][tileX];
	}

	public Tile getTile(int index) {
		int tileX = index % this.tileX;
		int tileY = index / this.tileX;
		return getTile(tileX, tileY);
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public int getNumberOfTiles() {
		return tileX * tileY;
	}

	private void transformToFourBitDepth() {
		// TODO
	}

	private void transformToEightBitDepth() {
		// TODO
	}

	private static Tile[] flatten(Tile[][] tiles) {
		int tileX = tiles[0].length;
		int tileY = tiles.length;
		Tile[] flatTiles = new Tile[tileX * tileY];

		int x = 0;
		int y = 0;
		for (int index = 0; index < flatTiles.length; index++) {
			flatTiles[index] = tiles[y][x];
			if (++x == tileX) {
				x = 0;
				y++;
			}
		}

		return flatTiles;
	}

	/**
	 * Resize the tiles to the new tileX and tileY and fill with the given flattened
	 * tiles array.
	 * 
	 * @param tiles          the tiles to resize
	 * @param flattenedTiles the flattened tiles to use as data
	 * @param tileX          the new tile X
	 * @param tileY          the new tile Y
	 * @return the resized tiles
	 */
	private static Tile[][] resize(Tile[] flattenedTiles, int oldTileX, int oldTileY, int newTileX, int newTileY) {
		int tileX = Math.max(oldTileX, newTileX);
		int tileY = Math.max(oldTileY, newTileY);
		Tile[][] newTiles = new Tile[newTileY][newTileX];

		// Get tiles to max index to ensure no overflow
		int maxIndex = Math.min(flattenedTiles.length, tileX * tileY);
		int x = 0;
		int y = 0;
		for (int index = 0; index < maxIndex; index++) {
			newTiles[y][x] = flattenedTiles[index];
			if (++x == tileX) {
				x = 0;
				y++;
			}
		}

		// Check if the new size is bigger
		if (tileX * tileY > maxIndex) {
			for (int index = maxIndex; index < tileX * tileY; index++) {
				newTiles[y][x] = Tile.DEFAULT_TILE;
				if (++x == tileX) {
					x = 0;
					y++;
				}
			}
		}

		return newTiles;
	}

	/**
	 * Resize the tiles to the new tileX and tileY.
	 * 
	 * @param tiles the tiles to resize
	 * @param tileX the new tile X
	 * @param tileY the new tile Y
	 * @return the resized tiles
	 */
	private static Tile[][] resize(Tile[][] tiles, int oldTileX, int oldTileY, int newTileX, int newTileY) {
		return resize(flatten(tiles), oldTileX, oldTileY, newTileX, newTileY);
	}

	public void setColorBitDepth(ColorBitDepth colorBitDepth) {
		if (colorBitDepth != this.colorBitDepth) {
			this.colorBitDepth = colorBitDepth;
			switch (colorBitDepth) {
			case FOUR_BIT_DEPTH:
				transformToFourBitDepth();
				break;

			case EIGHT_BIT_DEPTH:
				transformToEightBitDepth();
				break;
			}
		}
	}

	public ColorBitDepth getColorBitDepth() {
		return colorBitDepth;
	}
}
