package pokemon.frame.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import pokemon.event.Event;
import pokemon.event.EventListener;
import pokemon.event.palette.PaletteOpenedEvent;
import pokemon.event.palette.PaletteSelectedEvent;
import pokemon.event.tile.TilesOpenedEvent;
import pokemon.event.tile.TilesSelectedEvent;
import pokemon.event.ui.ArchiveExtractedEvent;
import pokemon.event.ui.FileDeletedEvent;
import pokemon.event.ui.TreeFileOpened;
import pokemon.files.FileFormat;
import pokemon.files.FormatEnum;
import pokemon.files.archive.NARC;
import pokemon.files.graphics.NCGR;
import pokemon.files.graphics.NCLR;
import pokemon.files.graphics.NSCR;
import pokemon.frame.panel.edition.PalettePanel;
import pokemon.frame.panel.edition.ScreenPanel;
import pokemon.frame.panel.edition.TilePanel;
import pokemon.logic.Palette;
import pokemon.logic.ScreenData;
import pokemon.logic.Tile;
import pokemon.manager.EventManager;

public class EditionPanel extends JDesktopPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3697366543301729496L;
	private static final int MAX_DISPLAY_X = 33;
	private static final int MAX_DISPLAY_Y = 26;
	private static final int OVERHEAD = 1;

	private Map<String, Palette> paletteMap;
	private Map<String, Tile[]> tilesMap;
	private Palette currentPalette;
	private Tile[] currentTiles;

	// Normal light grey panel but implements open events and opens internal frames
	public EditionPanel() {
		this.setBackground(Color.lightGray);

		// TODO Move this to OpenedResourceManager
		this.paletteMap = new HashMap<String, Palette>();
		this.tilesMap = new HashMap<String, Tile[]>();
		this.currentPalette = Palette.DEFAULT_PALETTE;
		this.currentTiles = Tile.DEFAULT_TILES;

		EventManager.getInstance().registerListener(this);
	}

	private void openInternalFrame(String title, JComponent panel) {
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JInternalFrame internalFrame = new JInternalFrame(title, true, true, false, true);
		internalFrame.setLayout(new BorderLayout());
		internalFrame.getContentPane().add(scroll);
		internalFrame.pack(); // Size defined by its panel

		this.add(internalFrame);
		internalFrame.setVisible(true);
	}

	private void openInternalPatternFrame(NCLR nclr, String paletteName) {
		// Do nothing if palette opened
		if (!paletteMap.containsKey(paletteName)) {
			Palette palette = nclr.createPalette();
			boolean isPaletteSelected = false;
			paletteMap.put(paletteName, palette);

			if (currentPalette == Palette.DEFAULT_PALETTE) {
				currentPalette = palette;
				isPaletteSelected = true;
			}

			EventManager.getInstance().throwEvent(new PaletteOpenedEvent(paletteName, palette, isPaletteSelected));
			PalettePanel panel = new PalettePanel(palette);
			openInternalFrame(paletteName, panel);
		}
	}

	private void openInternalTileFrame(NCGR ncgr, String tilesName) {
		if (!tilesMap.containsKey(tilesName)) {
			Tile[] tiles = ncgr.createTiles();
			boolean areTilesSelected = false;
			tilesMap.put(tilesName, tiles);

			if (currentTiles == Tile.DEFAULT_TILES) {
				currentTiles = tiles;
				areTilesSelected = true;
			}

			int tileX = ncgr.getTileX();
			int tileY = ncgr.getTileY();

			EventManager.getInstance().throwEvent(
					new TilesOpenedEvent(tilesName, ncgr.getColorBitDepth(), tileX, tileY, areTilesSelected));
			
			TilePanel panel = new TilePanel(tilesName, tiles, tileX, tileY);
			openInternalFrame(tilesName, panel);
		}
	}

	private void openInternalScreenFrame(NSCR nscr, String screenName) {
		ScreenData[] screenData = nscr.createScreenData();
		int screenWidth = nscr.getScreenWidth();
		int screenHeight = nscr.getScreenHeight();

		ScreenPanel panel = new ScreenPanel(screenData, currentPalette, currentTiles, screenWidth, screenHeight);
		openInternalFrame(screenName, panel);
	}

	private void extractArchive(File archiveFile) throws IOException {
		// Create stream for NARC
		InputStream inStream = new DataInputStream(new FileInputStream(archiveFile));

		// Create NARC and extract
		NARC narc = new NARC(archiveFile);
		narc.load(inStream);

		// Close stream
		inStream.close();

		// Notify UI that a new directory has been created
		Event event = new ArchiveExtractedEvent(archiveFile.toPath(), narc.getExtractDir().toPath());
		EventManager.getInstance().throwEvent(event);
		
		// Delete file
//		archiveFile.delete();
	}

	@Deprecated(forRemoval = true)
	public Palette getCurrentPalette() {
		return currentPalette;
	}

	@Deprecated(forRemoval = true)
	public Tile[] getCurrentTiles() {
		return currentTiles;
	}

	@EventListener
	public void onFileOpened(TreeFileOpened event) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int lastPointIndex = event.getFileName().lastIndexOf('.');
		String baseName = event.getFileName().substring(0, lastPointIndex);
		String extension = event.getFileName().substring(lastPointIndex + 1);

		// Get format from extension, if unknown just give up...
		FormatEnum format = FormatEnum.getFromExtension(extension.toLowerCase());
		if (format == FormatEnum.UNKNOWN) {
			return;
		} else if (format == FormatEnum.ARCHIVE) {
			// If it is an archive, check if the destination folder does not exist...
			File destDir = new File(event.getPath().toFile().getParent() + File.separator + baseName);
			if (destDir.exists()) {
				int answer = JOptionPane.showConfirmDialog(null,
						"The extracted folder already exist... Do you want to override it?", "Destination exists",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (answer == JOptionPane.NO_OPTION) {
					return;
				}

				// Notify deleted archive
				Event archiveDirectoryDeleted = new FileDeletedEvent(destDir);
				EventManager.getInstance().throwEvent(archiveDirectoryDeleted);
				
				// Delete directory
				try (Stream<Path> paths = Files.walk(destDir.toPath())) {
					paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
				}
			}

			// Extract archive
			extractArchive(event.getPath().toFile());
		} else {

			// Get empty constructor and create object
			Class<? extends FileFormat> formatClass = format.getFormatClass();
			FileFormat fileFormat = formatClass.getConstructor().newInstance();
			DataInputStream inStream = new DataInputStream(new FileInputStream(event.getPath().toFile()));
			fileFormat.load(inStream);
			inStream.close();

			switch (format) {
			case PALETTE:
				openInternalPatternFrame((NCLR) fileFormat, event.getFileName());
				break;

			case TILE:
				openInternalTileFrame((NCGR) fileFormat, event.getFileName());
				break;

			case SCREEN:
				openInternalScreenFrame((NSCR) fileFormat, event.getFileName());
				break;

			default:
				// Should not go here, must have been processed earlier
				break;
			}
		}
	}

	// TODO Remove, no global palette
	@EventListener
	public void onPaletteSelected(PaletteSelectedEvent event) {
		if (paletteMap.containsKey(event.getPaletteName())) {
			currentPalette = paletteMap.get(event.getPaletteName());
		}
	}

	// TODO Remove, no global tiles
	@EventListener
	public void onTileSelectedEvent(TilesSelectedEvent event) {
		if (tilesMap.containsKey(event.getTileName())) {
			currentTiles = tilesMap.get(event.getTileName());
		}
	}

}
