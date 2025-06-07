package pokemon.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import pokemon.event.EventListener;
import pokemon.event.palette.PaletteOpenedEvent;
import pokemon.logic.Palette;

public class OpenedResourceManager implements Manager {

	private static final OpenedResourceManager instance = new OpenedResourceManager();

	private final Map<String, Palette> openedPalettes;

	private OpenedResourceManager() {
		openedPalettes = new LinkedHashMap<String, Palette>();
		openedPalettes.put(null, Palette.DEFAULT_PALETTE);
	}

	public Palette openPalette(String paletteName, Palette palette) {
		return openedPalettes.put(paletteName, palette);
	}

	public Palette closePalette(String paletteName) {
		return openedPalettes.remove(paletteName);
	}

	public Palette getPalette(String paletteName) {
		return openedPalettes.get(paletteName);
	}
	
	public boolean hasOpenedPalette() {
		return openedPalettes.size() > 1;
	}
	
	public String[] getOpenedPalettesName() {
		return openedPalettes.keySet().stream().filter(t -> t != null).toArray(String[]::new);
	}

	@Override
	public void initManager() {
		// Register as event listener only now to ensure that all important listeners
		// are initialized
		EventManager.getInstance().registerListener(this);
	}

	public static OpenedResourceManager getInstance() {
		return instance;
	}
	
	@EventListener
	public void onPaletteOpened(PaletteOpenedEvent event) {
		openedPalettes.put(event.getPaletteName(), event.getOpenedPalette());
	}

}
