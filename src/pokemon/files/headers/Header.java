package pokemon.files.headers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.DSFileManager;
import pokemon.files.Loadable;
import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedArray;
import pokemon.files.types.TypedNumber;

public class Header implements Loadable {
	
	public static final int DEFAULT_HEADER_SIZE = 8;

	protected TypedArray magicId;
	protected TypedNumber sectionSize;

	public Header() {
		this.magicId = new TypedArray(TypeEnum.CHAR, 4);
		this.sectionSize = new TypedNumber(0, TypeEnum.UINT32);
	}
	
	public Header(String magicId, int sectionSize) {
		this.magicId = new TypedArray(magicId.substring(0, 4));
		this.sectionSize = new TypedNumber(sectionSize, TypeEnum.UINT32);
	}

	public TypedArray getMagicId() {
		return magicId;
	}

	public void setMagicId(String magicId) {
		this.magicId = new TypedArray(magicId);
	}

	public TypedNumber getSectionSize() {
		return sectionSize;
	}

	public void setSectionSize(int sectionSize) {
		this.sectionSize.setValue(sectionSize);
	}
	
	@Override
	public void load(InputStream inStream) throws IOException {
		DSFileManager.read(inStream, magicId);
		DSFileManager.read(inStream, sectionSize);
	}
	
	@Override
	public void store(OutputStream outStream) throws IOException {
		DSFileManager.write(outStream, magicId);
		DSFileManager.write(outStream, sectionSize);
	}

}
