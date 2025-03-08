package pokemon.panel.ui.properties;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class FormatProperties extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6139705999122841434L;
	
	protected void addSingle(JComponent component, GridBagConstraints c, int y) {
		c.gridx = 0;
		c.gridy = y;
		c.gridwidth = 2;
		this.add(component, c);
	}
	
	protected void addPair(JLabel label, JComponent component, GridBagConstraints c, int y) {
		c.gridwidth= 1;
		c.gridx = 0;
		c.gridy = y;
		this.add(label, c);
		
		c.gridx = 1;
		this.add(component, c);
	}


}
