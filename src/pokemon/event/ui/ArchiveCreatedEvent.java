package pokemon.event.ui;

import java.nio.file.Path;

import pokemon.event.Event;

public class ArchiveCreatedEvent implements Event {

	private Path archiveDir;
	private Path archivePath;

	public ArchiveCreatedEvent(Path archiveDir, Path archivePath) {
		this.archiveDir = archiveDir;
		this.archivePath = archivePath;
	}
	
	public Path getArchiveDir() {
		return archiveDir;
	}

	public Path getArchivePath() {
		return archivePath;
	}

}
