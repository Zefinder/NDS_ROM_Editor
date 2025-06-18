package pokemon.frame.panel.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pokemon.event.Event;
import pokemon.event.EventListener;
import pokemon.event.palette.PaletteOpenedEvent;
import pokemon.event.tile.TilesPaletteIndexChangedEvent;
import pokemon.event.tile.TilesPaletteSelectedEvent;
import pokemon.event.tile.TilesPerColumnChangedEvent;
import pokemon.event.tile.TilesPerRowChangedEvent;
import pokemon.event.tile.TilesPixelGridChangedEvent;
import pokemon.event.tile.TilesTileGridChangedEvent;
import pokemon.event.tile.TilesTransparentBackgroundChangedEvent;
import pokemon.event.tile.TilesZoomChangedEvent;
import pokemon.files.graphics.GraphicResources.ColorBitDepth;
import pokemon.manager.EventManager;
import pokemon.manager.OpenedResourceManager;

public class TilePropertiesPanel extends FormatProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -43428774942028367L;
	private static final DefaultComboBoxModel<Integer> FOUR_BITS_MODEL = new DefaultComboBoxModel<Integer>(
			new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
	private static final DefaultComboBoxModel<Integer> EIGHT_BITS_MODEL = new DefaultComboBoxModel<Integer>(
			new Integer[] { 0 });

	private DefaultComboBoxModel<String> model;
	private JComboBox<String> palettesList;
	private JComboBox<String> colorBitDepthList;
	private JComboBox<Integer> usedPaletteList;
	private JComboBox<Integer> zoomList;

	/**
	 * <p>
	 * Tile properties contain:
	 * 
	 * <ul>
	 * <li>Use as tiles -> button
	 * <li>Color bit depth -> combo box
	 * <li>Used palette -> combo box
	 * <li>Zoom -> combo box
	 * <li>Number of tiles in row -> text field
	 * <li>Number of tiles in column -> text field
	 * <li>Transparent background -> check box
	 * <li>Show tile grid -> check box
	 * <li>Show pixel grid -> check box
	 * </ul>
	 * </p>
	 */
	public TilePropertiesPanel(String tileName, ColorBitDepth colorBitDepth, int tileX, int tileY,
			boolean isTileSelected) {
		this.setBorder(BorderFactory.createTitledBorder(tileName));

		GridBagConstraints c = super.getDefaultConstraints();
		c.insets = new Insets(3, 8, 2, 8);

		// List of possible palettes
		String[] openedPalettes = OpenedResourceManager.getInstance().getOpenedPalettesName();
		model = new DefaultComboBoxModel<String>();
		String extractedTileName = OpenedResourceManager.getInstance().extractName(tileName);
		for (String paletteName : openedPalettes) {
			model.addElement(paletteName);
			if (paletteName.equals(extractedTileName)) {
				model.setSelectedItem(paletteName);
			}
		}

		palettesList = new JComboBox<String>(model);
		if (openedPalettes.length == 0) {
			palettesList.setEnabled(false);
		}
		palettesList.addActionListener(_ -> {
			String paletteName = (String) palettesList.getSelectedItem();
			Event event = new TilesPaletteSelectedEvent(tileName, paletteName);
			EventManager.getInstance().throwEvent(event);
		});

		// Color bit depth
		JLabel colorBitDepthLabel = new JLabel("Color bit depth", SwingConstants.LEFT);
		colorBitDepthList = new JComboBox<String>(new String[] { "4 bits", "8 bits" });
		colorBitDepthList.addActionListener(_ -> {
			if (colorBitDepthList.getSelectedIndex() == 0) {
				// TODO Send event to change mode for tiles
				usedPaletteList.setModel(FOUR_BITS_MODEL);
				
			} else {
				// TODO Send event to change mode for tiles
				usedPaletteList.setModel(EIGHT_BITS_MODEL);
			}
		});

		JLabel usedPaletteLabel = new JLabel("Palette n°");
		usedPaletteList = new JComboBox<Integer>();
		if (colorBitDepth == ColorBitDepth.FOUR_BIT_DEPTH) {
			usedPaletteList.setModel(FOUR_BITS_MODEL);
		} else {
			usedPaletteList.setModel(EIGHT_BITS_MODEL);
		}
		usedPaletteList.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesPaletteIndexChangedEvent(tileName, usedPaletteList.getSelectedIndex())));

		JLabel zoomLabel = new JLabel("Zoom", SwingConstants.LEFT);
		zoomList = new JComboBox<Integer>(new Integer[] { 1, 2, 3, 4, 5 });
		zoomList.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesZoomChangedEvent(tileName, (int) zoomList.getSelectedItem())));
		zoomList.setSelectedIndex(2);

		JLabel tilesInRowLabel = new JLabel("Tiles in a row", SwingConstants.LEFT);
		JFormattedTextField tilesInRow = new JFormattedTextField();
		tilesInRow.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesPerRowChangedEvent(tileName, Integer.valueOf(tilesInRow.getText()))));
		tilesInRow.setValue(tileX);

		JLabel tilesInColumnLabel = new JLabel("Tiles in a column", SwingConstants.LEFT);
		JFormattedTextField tilesInColumn = new JFormattedTextField();
		tilesInColumn.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesPerColumnChangedEvent(tileName, Integer.valueOf(tilesInColumn.getText()))));
		tilesInColumn.setValue(tileY);

		JCheckBox transparentBackground = new JCheckBox("Set transparent background");
		transparentBackground.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesTransparentBackgroundChangedEvent(tileName, transparentBackground.isSelected())));
		transparentBackground.setSelected(true);

		JCheckBox showTileGrid = new JCheckBox("Show tile grid");
		showTileGrid.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesTileGridChangedEvent(tileName, showTileGrid.isSelected())));
		showTileGrid.setSelected(true);

		JCheckBox showPixelGrid = new JCheckBox("Show pixel grid");
		showPixelGrid.addActionListener(_ -> EventManager.getInstance()
				.throwEvent(new TilesPixelGridChangedEvent(tileName, showPixelGrid.isSelected())));
		showPixelGrid.setSelected(false);

		int y = 0;
		addSingle(palettesList, c, y++);
		addPair(colorBitDepthLabel, colorBitDepthList, c, y++);
		addPair(usedPaletteLabel, usedPaletteList, c, y++);
		addPair(zoomLabel, zoomList, c, y++);
		addPair(tilesInRowLabel, tilesInRow, c, y++);
		addPair(tilesInColumnLabel, tilesInColumn, c, y++);
		addSingle(transparentBackground, c, y++);
		addSingle(showTileGrid, c, y++);
		addSingle(showPixelGrid, c, y++);

		EventManager.getInstance().registerListener(this);
	}

	@EventListener
	public void onPaletteOpened(PaletteOpenedEvent event) {
		model.addElement(OpenedResourceManager.getInstance().extractName(event.getPaletteName()));
		if (!palettesList.isEnabled()) {
			palettesList.setEnabled(true);
		}
	}

}
