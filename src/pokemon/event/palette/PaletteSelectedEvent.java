package pokemon.event.palette;

import pokemon.event.Event;

@Deprecated(forRemoval = true)
public class PaletteSelectedEvent implements Event {

	private String paletteName;

	public PaletteSelectedEvent(String paletteName) {
		this.paletteName = paletteName;
	}

	public String getPaletteName() {
		return paletteName;
	}

}
