package pokemon.event.tile;

import pokemon.files.graphics.GraphicResources.ColorBitDepth;

public class TilesOpenedEvent extends TilesEvent {

	private ColorBitDepth colorBitDepth;
	private int tileX;
	private int tileY;
	private boolean isSelected;

	public TilesOpenedEvent(String tileName, ColorBitDepth colorBitDepth, int tileX, int tileY, boolean isSelected) {
		super(tileName);
		this.colorBitDepth = colorBitDepth;
		this.tileX = tileX;
		this.tileY = tileY;
		this.isSelected = isSelected;
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
