package pokemon.files;

import pokemon.files.headers.Header;

public abstract class SubSection extends Section {

	public SubSection(String magicId, int sectionSize) {
		super(new Header(magicId, sectionSize));
	}

}
