package pokemon.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import pokemon.panel.ui.EditionPanel;
import pokemon.panel.ui.GraphicResourcesPanel;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3958782049071372085L;
	
	Dimension d;

	public MainFrame(String title, String projectDir) throws IOException {
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 800);
		this.setLocationRelativeTo(null);
		this.setResizable(true);

		// Three tabs:
		// - Graphic resources
		// - ARM7 code
		// - ARM9 code
		
		JPanel graphicResourcesPanel = new GraphicResourcesPanel(projectDir);
		this.add(graphicResourcesPanel, BorderLayout.CENTER);
		this.setVisible(false);
	}

	public void initFrame() {
		this.setVisible(true);
	}

}
