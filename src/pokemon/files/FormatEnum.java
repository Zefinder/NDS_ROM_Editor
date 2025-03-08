package pokemon.files;

import java.util.Map;

import pokemon.files.archive.NARC;
import pokemon.files.graphics.NCGR;
import pokemon.files.graphics.NCLR;
import pokemon.files.graphics.NSCR;
import pokemon.utils.MapBuilder;

public enum FormatEnum {
	
	PALETTE(NCLR.class), TILE(NCGR.class), SCREEN(NSCR.class), ARCHIVE(NARC.class), UNKNOWN(null);
	
	private static final Map<String, FormatEnum> extensionMap = new MapBuilder<String, FormatEnum>()
			.put("nclr", PALETTE)
			.put("rlcn", PALETTE)
			.put("ncgr", TILE)
			.put("rgcn", TILE)
			.put("nscr", SCREEN)
			.put("rcsn", SCREEN)
			.put("narc", ARCHIVE)
			.put("cran", ARCHIVE)
			.build();

	private Class<? extends FileFormat> formatClass;

	private FormatEnum(Class<? extends FileFormat> formatClass) {
		this.formatClass = formatClass;
	}
	
	public Class<? extends FileFormat> getFormatClass() {
		return formatClass;
	}
	
	public static FormatEnum getFromExtension(String extension) {
		return extensionMap.getOrDefault(extension, UNKNOWN);
	}
	
	public static String getExtensionFromMagic(String magic) {
		FormatEnum format = getFromExtension(magic.toLowerCase());
		String extension = "bin";
		
		if (format != UNKNOWN) {
			String className = format.getFormatClass().getName().toLowerCase();
			extension = className.substring(className.lastIndexOf(".") + 1);
		}
		
		return extension;
	}

}
