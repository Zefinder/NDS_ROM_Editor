package pokemon.event.tile;

import pokemon.event.Event;

public abstract class TilesEvent implements Event {

	private String tileName;
	
	public TilesEvent(String tileName) {
		this.tileName = tileName;
	}
	
	public String getTileName() {
		return tileName;
	}
	
}
