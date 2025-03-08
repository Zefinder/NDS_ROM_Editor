package pokemon.panel.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class GraphicResourcesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2087142680610861975L;

	public GraphicResourcesPanel(String projectDirPath) throws IOException {
		// White background
		this.setBackground(Color.white);
		
		// Border layout to fill all the space
		this.setLayout(new BorderLayout());

		// Contains 3 components:
		// - Left -> The file tree from the root
		// - Center -> Area for internal frames
		// - Right -> Properties

		JPanel treePanel = new TreeFilePanel(new File(projectDirPath));
		JScrollPane scrollTreePanel = new JScrollPane(treePanel);
		scrollTreePanel.getVerticalScrollBar().setUnitIncrement(16);
		scrollTreePanel.setBackground(Color.white);

		JDesktopPane editionPanel = new EditionPanel();
		JPanel propertiesPanel = new PropertiesPanel();

		JSplitPane treeEditorPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scrollTreePanel, editionPanel);
		treeEditorPane.setOneTouchExpandable(true);
		treeEditorPane.setResizeWeight(0.15);
		treeEditorPane.setBackground(Color.white);

		JSplitPane editorPropertiesPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treeEditorPane, propertiesPanel);
		editorPropertiesPane.setOneTouchExpandable(true);
		editorPropertiesPane.setResizeWeight(0.80);
		editorPropertiesPane.setBackground(Color.white);
		
		this.add(editorPropertiesPane);
	}

}
