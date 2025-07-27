package pokemon.event.screen;

public class ScreenTilesSelectedEvent extends ScreenEvent {

	private String tilesName;
	
	public ScreenTilesSelectedEvent(String screenName, String tilesName) {
		super(screenName);
		this.tilesName = tilesName;
	}
	
	public String getTilesName() {
		return tilesName;
	}

}
