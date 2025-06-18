package pokemon.event.tile;

public class TilesTransparentBackgroundChangedEvent extends TilesEvent {

	private boolean showTransparentBackground;

	public TilesTransparentBackgroundChangedEvent(String tileName, boolean showTransparentBackground) {
		super(tileName);
		this.showTransparentBackground = showTransparentBackground;
	}

	public boolean isShowTransparentBackground() {
		return showTransparentBackground;
	}

}
