package pokemon.panel.ui.popup;

import java.io.File;
import java.nio.file.Path;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pokemon.frame.ArchiveDirDialog;

public class DirPopup extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8827603045576524850L;

	public DirPopup(Path dirPath) {
		// Archive menu
		JMenuItem archiveItem = new JMenuItem("Transform to archive");
		archiveItem.addActionListener(_ -> {
			File archiveFile = new File(dirPath.toString() + ".narc");
//			System.out.println("Hello! %s".formatted(archiveFile.toString()));
			ArchiveDirDialog dialog = new ArchiveDirDialog(dirPath, archiveFile.toPath());
			dialog.initDialog();
		});
		this.add(archiveItem);
	}
}
