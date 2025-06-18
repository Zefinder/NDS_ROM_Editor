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
		return openedPalettes.put(extractName(paletteName), palette);
	}

	public Palette closePalette(String paletteName) {
		return openedPalettes.remove(extractName(paletteName));
	}

	public Palette getPalette(String paletteName) {
		return openedPalettes.get(extractName(paletteName));
	}

	public Palette getPaletteOrAvailable(String paletteName) {
		Palette palette = getPalette(extractName(paletteName));

		if (palette == null) {
			if (hasOpenedPalette()) {
				palette = getPalette(getOpenedPalettesName()[0]);
			} else {
				palette = getPalette(null);
			}
		}

		return palette;
	}

	public boolean hasOpenedPalette() {
		return openedPalettes.size() > 1;
	}

	public String[] getOpenedPalettesName() {
		return openedPalettes.keySet().stream().filter(t -> t != null).map(t -> {
			int slashIndex = t.lastIndexOf('/');
			if (slashIndex != -1) {
				t = t.substring(0, slashIndex);
			}
			
			return extractName(t);
		}).toArray(String[]::new);
	}

	public String extractName(String name) {
		if (name == null) {
			return null;
		}
		
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex != -1) {
			name = name.substring(0, dotIndex);
		}
		
		return name;
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
		openPalette(event.getPaletteName(), event.getOpenedPalette());
	}

}
