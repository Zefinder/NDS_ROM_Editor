package pokemon.event.tile;

public class TilesTileGridChangedEvent extends TilesEvent {

	private boolean showTileGrid;
	
	public TilesTileGridChangedEvent(String tileName, boolean showTileGrid) {
		super(tileName);
		this.showTileGrid = showTileGrid;
	}
	
	public boolean isShowTileGrid() {
		return showTileGrid;
	}

}
