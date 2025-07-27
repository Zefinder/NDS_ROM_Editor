package pokemon.event.screen;

public class ScreenPaletteSelectedEvent extends ScreenEvent {

	private String paletteName;
	
	public ScreenPaletteSelectedEvent(String screenName, String paletteName) {
		super(screenName);
		this.paletteName = paletteName;
	}
	
	public String getPaletteName() {
		return paletteName;
	}

}
