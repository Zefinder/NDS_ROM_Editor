package pokemon.event.palette;

import java.awt.Color;

import pokemon.event.Event;

public class PaletteColorModifiedEvent implements Event {

	private int paletteNumber;
	private int index;
	private Color oldColor;
	private Color newColor;
	
	public PaletteColorModifiedEvent(int paletteNumber, int index, Color oldColor, Color newColor) {
		this.paletteNumber = paletteNumber;
		this.index = index;
		this.oldColor = oldColor;
		this.newColor = newColor;
	}
	
	public int getPaletteNumber() {
		return paletteNumber;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Color getOldColor() {
		return oldColor;
	}
	
	public Color getNewColor() {
		return newColor;
	}
	
}
