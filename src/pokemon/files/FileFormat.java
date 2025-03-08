package pokemon.files;

import pokemon.files.headers.GenericHeader;

public abstract class FileFormat extends Section {

	public FileFormat(String magicId, int sectionSize, int subsectionNumber) {
		super(new GenericHeader(magicId, GenericHeader.DEFAULT_HEADER_SIZE, sectionSize, subsectionNumber));
	}

	protected int getSubsectionNumber() {
		return ((GenericHeader) header).getSubsectionNumber();
	}

	protected void setSubsectionNumber(int subsectionNumber) {
		((GenericHeader) header).setSubsectionNumber(subsectionNumber);
	}

	protected void setByteOrder(int byteOrder) {
		((GenericHeader) header).setByteOrder(byteOrder);
	}

	protected void setVersionNumber(int versionNumber) {
		((GenericHeader) header).setVersionNumber(versionNumber);
	}

	protected abstract void updateSize();

}
