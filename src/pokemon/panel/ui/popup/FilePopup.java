package pokemon.panel.ui.popup;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

public class FilePopup extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8407979008729217145L;

	public FilePopup() {
		JMenuItem mnuUndo = new JMenuItem("Undo");
		mnuUndo.setMnemonic('U');
		mnuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
		this.add(mnuUndo);
	}

}
