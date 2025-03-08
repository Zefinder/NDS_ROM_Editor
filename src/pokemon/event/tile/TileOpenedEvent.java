package pokemon.event.tile;

import pokemon.event.Event;
import pokemon.files.graphics.GraphicResources.ColorBitDepth;

public class TileOpenedEvent implements Event {

	private String tileName;
	private ColorBitDepth colorBitDepth;
	private int tileX;
	private int tileY;
	private boolean isSelected;

	public TileOpenedEvent(String tileName, ColorBitDepth colorBitDepth, int tileX, int tileY, boolean isSelected) {
		this.tileName = tileName;
		this.colorBitDepth = colorBitDepth;
		this.tileX = tileX;
		this.tileY = tileY;
		this.isSelected = isSelected;
	}

	public String getTileName() {
		return tileName;
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
	
	public boolean isSelected() {
		return isSelected;
	}

}
