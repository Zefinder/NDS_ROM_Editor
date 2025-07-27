package pokemon.event.palette;

import pokemon.event.Event;
import pokemon.logic.Palette;

public class PaletteOpenedEvent implements Event {
	
	private String paletteName;
	private Palette openedPalette;
	
	public PaletteOpenedEvent(String paletteName, Palette openedPalette) {
		this.paletteName = paletteName;
		this.openedPalette = openedPalette;
	}
	
	public String getPaletteName() {
		return paletteName;
	}
	
	public Palette getOpenedPalette() {
		return openedPalette;
	}
}
