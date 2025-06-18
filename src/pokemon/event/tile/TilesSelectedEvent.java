package pokemon.event.tile;

import pokemon.event.Event;

@Deprecated(forRemoval = true)
public class TilesSelectedEvent implements Event {
	
	private String tileName;
	
	public TilesSelectedEvent(String tileName) {
		this.tileName = tileName;
	}
	
	public String getTileName() {
		return tileName;
	}

}
