package pokemon.event.tile;

import pokemon.event.Event;

@Deprecated(forRemoval = true)
public class TilePropertiesChangedEvent implements Event {
	
	public enum TilePropertyChanged {
		ZOOM, SELECTED_PALETTE, TILE_X, TILE_Y, TRANSPARENT_BG, SHOW_TILE_GRID, SHOW_PIXEL_GRID;
	}
	
	private String tileName;
	private TilePropertyChanged property;
	private int value;
	
	public TilePropertiesChangedEvent(String tileName, TilePropertyChanged property, int value) {
		this.tileName = tileName;
		this.property = property;
		this.value = value;
	}
	
	public String getTileName() {
		return tileName;
	}
	
	public TilePropertyChanged getProperty() {
		return property;
	}
	
	public int getValue() {
		return value;
	}
}
