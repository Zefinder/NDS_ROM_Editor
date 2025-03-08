package pokemon.frame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import pokemon.event.Event;
import pokemon.event.EventManager;
import pokemon.event.ui.ArchiveCreatedEvent;
import pokemon.files.archive.CompressionMethodEnum;
import pokemon.files.archive.NARC;

public class ArchiveDirDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2979386756588702611L;

	public ArchiveDirDialog(Path archiveDir, Path archivePath) {
		this.setTitle("Create archive");
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setModalityType(ModalityType.APPLICATION_MODAL);

		/*
		 * The dialog will contain the destination path, compression method and write
		 * sub-tables
		 */
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(3, 8, 2, 8);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;

		// Destination path
		c.gridx = 0;
		c.gridy = 0;
		JLabel destinationPathLabel = new JLabel("Destination path");
		JTextField destinationPath = new JTextField(archivePath.toString());
		destinationPath.setEnabled(false);

		this.add(destinationPathLabel, c);
		c.gridx = 1;
		this.add(destinationPath, c);

		// Compression method
		c.gridx = 0;
		c.gridy = 1;
		// TODO Use compression
		JLabel compressionMethodLabel = new JLabel("Compression method");
		JComboBox<CompressionMethodEnum> compressionMethod = new JComboBox<CompressionMethodEnum>(
				CompressionMethodEnum.values());
		this.add(compressionMethodLabel, c);
		c.gridx = 1;
		this.add(compressionMethod, c);

		// Sub-tables
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		JCheckBox doWriteSubtables = new JCheckBox("Write sub-tables?");
		doWriteSubtables.setSelected(false);
		doWriteSubtables.setPreferredSize(doWriteSubtables.getMinimumSize());
		this.add(doWriteSubtables, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		JButton createArchiveButton = new JButton("Create archive");
		createArchiveButton.addActionListener(_ -> {
			// Check if archive already exist, ask if want to delete it
			File archiveFile = archivePath.toFile();
			if (archiveFile.exists()) {
				int answer = JOptionPane.showConfirmDialog(null, "Archive already exists, do you want to override it?",
						"Archive already exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer != JOptionPane.YES_OPTION) {
					return;
				}
				
				archiveFile.delete();
			}

			NARC narc = new NARC(archivePath.toFile(), doWriteSubtables.isSelected());
			try {
				// Create archive
				narc.createArchive();

				// TODO Delete folder

				// Notify UI that tree has been modified
				Event archiveCreatedEvent = new ArchiveCreatedEvent(archiveDir, archivePath);
				EventManager.getInstance().throwEvent(archiveCreatedEvent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			dispose();
		});
		this.add(createArchiveButton, c);

		this.setVisible(false);
	}

	public void initDialog() {
		this.setVisible(true);
	}

}
