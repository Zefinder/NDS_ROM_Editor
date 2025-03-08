package pokemon.logic;

public class Tile {

	public static final int TILE_SIZE = 8;
	public static final Tile[] DEFAULT_TILES = new Tile[0];
	
	private int[][] tileData;
	
	public Tile() {
		// Inits at 0
		this.tileData = new int[TILE_SIZE][TILE_SIZE];
	}
	
	public Tile(int[] tileData) {
		this();
		
		// Filling the data
		int x = 0;
		int y = 0;
		for (int paletteIndex : tileData) {
			this.tileData[x][y] = paletteIndex;
			
			if (++x == TILE_SIZE) {
				x = 0;
				y += 1;
			}
		}
	}
	
	public int getData(int x, int y) {
		return tileData[x][y];
	}
	
	public void setData(int x, int y, int paletteIndex) {
		tileData[x][y] = paletteIndex;
	}
	
	public int[][] getTileData() {
		return tileData;
	}
}
