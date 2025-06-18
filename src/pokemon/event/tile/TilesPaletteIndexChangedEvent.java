package pokemon.event.tile;

public class TilesPaletteIndexChangedEvent extends TilesEvent {
	
	private int paletteIndex;
	
	public TilesPaletteIndexChangedEvent(String tileName, int paletteIndex) {
		super(tileName);
		this.paletteIndex = paletteIndex;
	}
	
	public int getPaletteIndex() {
		return paletteIndex;
	}

}
