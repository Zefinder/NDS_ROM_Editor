package pokemon.event.tile;

import pokemon.event.Event;

public class TileSelectedEvent implements Event {
	
	private String tileName;
	
	public TileSelectedEvent(String tileName) {
		this.tileName = tileName;
	}
	
	public String getTileName() {
		return tileName;
	}

}
