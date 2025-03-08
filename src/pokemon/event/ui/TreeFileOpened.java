package pokemon.event.ui;

import java.nio.file.Path;

import pokemon.event.Event;

public class TreeFileOpened implements Event {
	
	private Path path;
	private String fileName;
	
	public TreeFileOpened(Path path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}
	
	public Path getPath() {
		return path;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
