package pokemon.event.tile;

import pokemon.event.Event;

public class TilePixelModifiedEvent implements Event {

	private int tileIndex;
	private int xPixelIndex;
	private int yPixelIndex;
	private int oldColorIndex;
	private int newColorIndex;

	public TilePixelModifiedEvent(int tileIndex, int xPixelIndex, int yPixelIndex, int oldColorIndex, int newColorIndex) {
		this.tileIndex = tileIndex;
		this.xPixelIndex = xPixelIndex;
		this.yPixelIndex = yPixelIndex;
		this.oldColorIndex = oldColorIndex;
		this.newColorIndex = newColorIndex;
	}
	
	public int getTileIndex() {
		return tileIndex;
	}
	
	public int getxPixelIndex() {
		return xPixelIndex;
	}
	
	public int getyPixelIndex() {
		return yPixelIndex;
	}
	
	public int getOldColorIndex() {
		return oldColorIndex;
	}
	
	public int getNewColorIndex() {
		return newColorIndex;
	}
}
