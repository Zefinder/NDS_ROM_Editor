package pokemon.panel.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import pokemon.event.EventListener;
import pokemon.event.EventManager;
import pokemon.event.palette.PaletteOpenedEvent;
import pokemon.event.tile.TileOpenedEvent;
import pokemon.panel.ui.properties.PalettePropertiesPanel;
import pokemon.panel.ui.properties.TilePropertiesPanel;

public class PropertiesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7815141374709772925L;

	private Map<String, JPanel> openProperties;
	private JPanel propertiesPanel;

	private int yIndex;
	private GridBagConstraints c;

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
		this.revalidate();
		this.repaint();
	}

	@EventListener
	public void onPaletteOpened(PaletteOpenedEvent event) {
		// Cannot create twice the same properties
		if (!openProperties.containsKey(event.getPaletteName())) {
			JPanel paletteProperties = new PalettePropertiesPanel(event.getPaletteName(), event.getOpenedPalette(),
					event.isPaletteSelected());
			openProperties.put(event.getPaletteName(), paletteProperties);
			addProperty(paletteProperties);
		}
	}

	@EventListener
	public void onTileOpened(TileOpenedEvent event) {
		// Cannot create twice the same properties
		if (!openProperties.containsKey(event.getTileName())) {
			JPanel tileProperties = new TilePropertiesPanel(event.getTileName(), event.getColorBitDepth(),
					event.getTileX(), event.getTileY(), event.isSelected());
			openProperties.put(event.getTileName(), tileProperties);
			addProperty(tileProperties);
		}
	}
}
