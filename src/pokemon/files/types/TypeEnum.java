package pokemon.files.types;

public enum TypeEnum {
	UINT8(1), UINT16(2), UINT32(4), UINT64(8), INT8(1), INT16(2), INT32(4), INT64(8), CHAR(1), BOOL(1), FLOAT(4),
	DOUBLE(8);

	private int byteSize;

	private TypeEnum(final int byteSize) {
		this.byteSize = byteSize;
	}

	public int getByteSize() {
		return byteSize;
	}

}
