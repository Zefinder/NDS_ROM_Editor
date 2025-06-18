package pokemon.event.tile;

public class TilesPerRowChangedEvent extends TilesEvent {

	private int tileY;
	
	public TilesPerRowChangedEvent(String tileName, int tileY) {
		super(tileName);
		this.tileY = tileY;
	}
	
	public int getTileY() {
		return tileY;
	}

}
