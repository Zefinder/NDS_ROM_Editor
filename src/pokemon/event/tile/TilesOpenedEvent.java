package pokemon.event.tile;

import pokemon.files.graphics.GraphicResources.ColorBitDepth;
import pokemon.logic.Tiles;

public class TilesOpenedEvent extends TilesEvent {

	private Tiles tiles;
	private ColorBitDepth colorBitDepth;
	private int tileX;
	private int tileY;

	public TilesOpenedEvent(String tileName, Tiles tiles) {
		super(tileName);
		this.tiles = tiles;

		this.colorBitDepth = tiles.getColorBitDepth();
		this.tileX = tiles.getTileX();
		this.tileY = tiles.getTileY();
	}
	
	public Tiles getTiles() {
		return tiles;
	}

	public ColorBitDepth getColorBitDepth() {
		return colorBitDepth;
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}
}
