package pokemon.files.headers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.DSFileManager;
import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedNumber;

public class GenericHeader extends Header {

	public static final int DEFAULT_HEADER_SIZE = 0x10;

	private TypedNumber headerSize;
	private TypedNumber byteOrder;
	private TypedNumber versionNumber;
	private TypedNumber subsectionNumber;

	public GenericHeader() {
		super();
		this.headerSize = new TypedNumber(0, TypeEnum.UINT16);
		this.subsectionNumber = new TypedNumber(0, TypeEnum.UINT16);
		this.byteOrder = new TypedNumber(0xFEFF, TypeEnum.UINT16);
		this.versionNumber = new TypedNumber(0x0100, TypeEnum.UINT16);
	}

	public GenericHeader(String magicId, int headerSize, int sectionSize, int subsectionNumber) {
		super(magicId, sectionSize);
		this.headerSize = new TypedNumber(headerSize, TypeEnum.UINT16);
		this.subsectionNumber = new TypedNumber(subsectionNumber, TypeEnum.UINT16);
		this.byteOrder = new TypedNumber(0xFEFF, TypeEnum.UINT16);
		this.versionNumber = new TypedNumber(0x0100, TypeEnum.UINT16);
	}

	public int getHeaderSize() {
		return headerSize.getIntValue();
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize.setValue(headerSize);
	}

	public int getSubsectionNumber() {
		return subsectionNumber.getIntValue();
	}

	public void setSubsectionNumber(int subsectionNumber) {
		this.subsectionNumber.setValue(subsectionNumber);
	}
	
	public void setByteOrder(int byteOrder) {
		this.byteOrder.setValue(byteOrder);
	}
	
	public void setVersionNumber(int versionNumber) {
		this.versionNumber.setValue(versionNumber);
	}

	@Override
	public void load(InputStream inStream) throws IOException {
		DSFileManager.read(inStream, magicId);
		DSFileManager.read(inStream, byteOrder);
		DSFileManager.read(inStream, versionNumber);
		DSFileManager.read(inStream, sectionSize);
		DSFileManager.read(inStream, headerSize);
		DSFileManager.read(inStream, subsectionNumber);
	}

	@Override
	public void store(OutputStream outStream) throws IOException {
		DSFileManager.write(outStream, magicId);
		DSFileManager.write(outStream, byteOrder);
		DSFileManager.write(outStream, versionNumber);
		DSFileManager.write(outStream, sectionSize);
		DSFileManager.write(outStream, headerSize);
		DSFileManager.write(outStream, subsectionNumber);
	}

}
