package pokemon.event.tile;

import pokemon.event.Event;

public class TileEditSelectedEvent implements Event {
	
	private int tileIndex;
	private int[][] tileData;
	
	public TileEditSelectedEvent(int tileIndex, int[][] tileData) {
		this.tileIndex = tileIndex;
		this.tileData = tileData;
	}
	
	public int getTileIndex() {
		return tileIndex;
	}
	
	public int[][] getTileData() {
		return tileData;
	}
	
}
