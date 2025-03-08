package pokemon.logic;

public class TileInformation {

	private Tile[] tiles;
	private int tileBitDepth;
	private int tileX;
	
	/**
	 * Stores all tiles of a tile file and the number of tiles in a row
	 * 
	 * @param tiles tiles of the pattern file
	 * @param tileX number of tiles in a row
	 */
	public TileInformation(Tile[] tiles, int tileBitDepth, int tileX) {
		this.tiles = tiles;
		this.tileBitDepth = tileBitDepth;
		this.tileX = tileX;
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
	
	public int getTileBitDepth() {
		return tileBitDepth;
	}
	
	public int getTileX() {
		return tileX;
	}

}
