package pokemon.frame.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import pokemon.event.EventListener;
import pokemon.event.palette.PaletteOpenedEvent;
import pokemon.event.tile.TilesOpenedEvent;
import pokemon.frame.panel.properties.PalettePropertiesPanel;
import pokemon.frame.panel.properties.TilePropertiesPanel;
import pokemon.manager.EventManager;

public class PropertiesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7815141374709772925L;

	private Map<String, JPanel> openProperties;
	private JPanel propertiesPanel;

	private int yIndex;
	private GridBagConstraints c;

	// TODO Create types of panels (palette panels, tile panels, etc...)

	public PropertiesPanel() {
		openProperties = new HashMap<String, JPanel>();
		this.setLayout(new BorderLayout());

		JPanel globalPanel = new JPanel();
		propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new GridBagLayout());
		globalPanel.add(propertiesPanel);

		JScrollPane scroll = new JScrollPane(globalPanel);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getHorizontalScrollBar().setUnitIncrement(16);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		this.add(scroll);

		c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		yIndex = 0;

		EventManager.getInstance().registerListener(this);
	}

	private void addProperty(JPanel panel) {
		c.gridy = yIndex++;
		propertiesPanel.add(panel, c);
		this.validate();
	}

	@EventListener
	public void onPaletteOpened(PaletteOpenedEvent event) {
		// Cannot create twice the same properties
		if (!openProperties.containsKey(event.getPaletteName())) {
			JPanel paletteProperties = new PalettePropertiesPanel(event.getPaletteName(), event.getOpenedPalette());
			openProperties.put(event.getPaletteName(), paletteProperties);
			addProperty(paletteProperties);
			// TODO Send event palette properties added
		}
	}

	@EventListener
	public void onTileOpened(TilesOpenedEvent event) {
		// Cannot create twice the same properties
		if (!openProperties.containsKey(event.getTileName())) {
			JPanel tileProperties = new TilePropertiesPanel(event.getTileName(), event.getColorBitDepth(),
					event.getTileX(), event.getTileY());
			openProperties.put(event.getTileName(), tileProperties);
			addProperty(tileProperties);
			// TODO Send event tile properties added
		}
	}
}
