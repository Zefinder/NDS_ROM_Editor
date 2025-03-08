package pokemon.event.ui;

import java.io.File;

import pokemon.event.Event;

public class FileDeletedEvent implements Event {

	private File deletedFile;

	public FileDeletedEvent(File deletedFile) {
		this.deletedFile = deletedFile;
	}

	public File getDeletedFile() {
		return deletedFile;
	}

}
