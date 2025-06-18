package pokemon.event.tile;

public class TilesPerColumnChangedEvent extends TilesEvent {

	private int tileX;
	
	public TilesPerColumnChangedEvent(String tileName, int tileX) {
		super(tileName);
		this.tileX = tileX;
	}

	public int getTileX() {
		return tileX;
	}
	
}
