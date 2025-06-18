package pokemon.event.tile;

public class TilesZoomChangedEvent extends TilesEvent {

	private int zoom;
	
	public TilesZoomChangedEvent(String tileName, int zoom) {
		super(tileName);
		this.zoom = zoom;
	}
	
	public int getZoom() {
		return zoom;
	}
	
}
