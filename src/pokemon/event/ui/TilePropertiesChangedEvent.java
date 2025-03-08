package pokemon.event.ui;

import pokemon.event.Event;

public class TilePropertiesChangedEvent implements Event {
	
	public enum ChangedProperty {
		ZOOM, SELECTED_PALETTE, TILE_X, TILE_Y, TRANSPARENT_BG, SHOW_TILE_GRID, SHOW_PIXEL_GRID;
	}
	
	private String tileName;
	private ChangedProperty property;
	private int value;
	
	public TilePropertiesChangedEvent(String tileName, ChangedProperty property, int value) {
		this.tileName = tileName;
		this.property = property;
		this.value = value;
	}
	
	public String getTileName() {
		return tileName;
	}
	
	public ChangedProperty getProperty() {
		return property;
	}
	
	public int getValue() {
		return value;
	}
}
