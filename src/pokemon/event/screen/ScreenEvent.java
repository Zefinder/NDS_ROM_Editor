package pokemon.event.screen;

import pokemon.event.Event;

public abstract class ScreenEvent implements Event {

	private String screenName;
	
	public ScreenEvent(String screenName) {
		this.screenName = screenName;
	}
	
	public String getScreenName() {
		return screenName;
	}

}
