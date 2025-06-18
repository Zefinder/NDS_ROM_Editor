package pokemon.event.tile;

import pokemon.files.graphics.GraphicResources.ColorBitDepth;

public class TilesPaletteColorDepthChangedEvent extends TilesEvent {

	private ColorBitDepth colorBitDepth;

	public TilesPaletteColorDepthChangedEvent(String tileName, ColorBitDepth colorBitDepth) {
		super(tileName);
		this.colorBitDepth = colorBitDepth;
	}
	
	public ColorBitDepth getColorBitDepth() {
		return colorBitDepth;
	}

}
