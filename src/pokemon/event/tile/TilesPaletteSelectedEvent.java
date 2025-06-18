package pokemon.event.tile;

public class TilesPaletteSelectedEvent extends TilesEvent {

	private String paletteName;
	
	public TilesPaletteSelectedEvent(String tileName, String paletteName) {
		super(tileName);
		this.paletteName = paletteName;
	}
	
	public String getPaletteName() {
		return paletteName;
	}

}
