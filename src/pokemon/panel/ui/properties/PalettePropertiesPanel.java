package pokemon.panel.ui.properties;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pokemon.event.EventListener;
import pokemon.event.EventManager;
import pokemon.event.palette.PaletteSelectedEvent;
import pokemon.files.graphics.GraphicResources.ColorBitDepth;
import pokemon.logic.Palette;

public class PalettePropertiesPanel extends FormatProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5417941260463654142L;
	private static final DefaultComboBoxModel<Integer> FOUR_BITS_MODEL = new DefaultComboBoxModel<Integer>(
			new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 });
	private static final DefaultComboBoxModel<Integer> EIGHT_BITS_MODEL = new DefaultComboBoxModel<Integer>(
			new Integer[] { 1 });

	private String paletteName;

	private JButton paletteSelectionButton;
	private JComboBox<String> colorBitDepthList;
	private JComboBox<Integer> paletteNumberList;

	/**
	 * <p>
	 * Palette properties contains:
	 * <ul>
	 * <li>Use as palette -> button
	 * <li>Color bit depth (4 bits or 8 bits) -> combobox
	 * <li>Number of palettes (1 to 16 for 4 bits, only one for 8 bits) -> combobox?
	 * </ul>
	 * </p>
	 */
	public PalettePropertiesPanel(String paletteName, Palette palette, boolean isPaletteSelected) {
		this.paletteName = paletteName;
		this.setBorder(BorderFactory.createTitledBorder(paletteName));

		// Check if 4 bits depth
		boolean isFourBitsDepth = palette.getBitDepth() == ColorBitDepth.FOUR_BIT_DEPTH.getBitDepthValue();

		// Use palette button
		paletteSelectionButton = new JButton("Use palette");
		paletteSelectionButton
				.addActionListener(_ -> EventManager.getInstance().throwEvent(new PaletteSelectedEvent(paletteName)));
		paletteSelectionButton.setEnabled(!isPaletteSelected);

		// Color bit depth, changing color depth does not change anything to the display
		JLabel colorBitDepthLabel = new JLabel("Color bit depth", SwingConstants.CENTER);
		colorBitDepthList = new JComboBox<String>(new String[] { "4 bits", "8 bits" });
		colorBitDepthList.setSelectedIndex(isFourBitsDepth ? 0 : 1);
		colorBitDepthList.addActionListener(_ -> {
			if (colorBitDepthList.getSelectedIndex() == 0) {
				palette.setBitDepth(ColorBitDepth.FOUR_BIT_DEPTH.getBitDepthValue());
				paletteNumberList.setModel(FOUR_BITS_MODEL);
			} else {
				palette.setBitDepth(ColorBitDepth.EIGHT_BIT_DEPTH.getBitDepthValue());
				paletteNumberList.setModel(EIGHT_BITS_MODEL);
			}
		});

		// Number of palettes
		JLabel paletteNumberLabel = new JLabel("Number of palette");
		paletteNumberList = new JComboBox<Integer>(isFourBitsDepth ? FOUR_BITS_MODEL : EIGHT_BITS_MODEL);
		paletteNumberList.addActionListener(_ -> {
			palette.setPaletteNumber((int) paletteNumberList.getSelectedItem());
		});
		paletteNumberList.setSelectedIndex(palette.getPaletteNumber() - 1);

		// Place all items
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(3, 8, 2, 8);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;

		int y = 0;
		addSingle(paletteSelectionButton, c, y++);
		addPair(colorBitDepthLabel, colorBitDepthList, c, y++);
		addPair(paletteNumberLabel, paletteNumberList, c, y++);

		EventManager.getInstance().registerListener(this);
	}

	@EventListener
	public void onPaletteSelected(PaletteSelectedEvent event) {
		paletteSelectionButton.setEnabled(!paletteName.equals(event.getPaletteName()));
		repaint();
	}

}
