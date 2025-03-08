package pokemon.event.palette;

import pokemon.event.Event;
import pokemon.logic.Palette;

public class PaletteOpenedEvent implements Event {
	
	private String paletteName;
	private Palette openedPalette;
	private boolean isPaletteSelected;
	
	public PaletteOpenedEvent(String paletteName, Palette openedPalette, boolean isPaletteSelected) {
		this.paletteName = paletteName;
		this.openedPalette = openedPalette;
		this.isPaletteSelected = isPaletteSelected;
	}
	
	public String getPaletteName() {
		return paletteName;
	}
	
	public Palette getOpenedPalette() {
		return openedPalette;
	}
	
	public boolean isPaletteSelected() {
		return isPaletteSelected;
	}

}
