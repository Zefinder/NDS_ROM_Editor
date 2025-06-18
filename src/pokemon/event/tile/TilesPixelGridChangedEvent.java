package pokemon.event.tile;

public class TilesPixelGridChangedEvent extends TilesEvent {

	private boolean showPixelGrid;
	
	public TilesPixelGridChangedEvent(String tileName, boolean showPixelGrid) {
		super(tileName);
		this.showPixelGrid = showPixelGrid;
	}

	public boolean isShowPixelGrid() {
		return showPixelGrid;
	}
	
}
