package pokemon.event.ui;

import java.nio.file.Path;

import pokemon.event.Event;

public class ArchiveExtractedEvent implements Event {

	private Path archivePath;
	private Path extractedPath;
	
	public ArchiveExtractedEvent(Path archivePath, Path extractedPath) {
		this.archivePath = archivePath;
		this.extractedPath = extractedPath;
	}
	
	public Path getArchivePath() {
		return archivePath;
	}
	
	public Path getExtractedPath() {
		return extractedPath;
	}
	
}
