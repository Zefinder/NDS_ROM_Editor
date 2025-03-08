package pokemon.files.types;

public class TypedNumber extends TypedVariable {

	private long value;

	public TypedNumber(long value, TypeEnum type) {
		super(type);
		setValue(value);
	}

	public TypedNumber(float value) {
		super(TypeEnum.FLOAT);
		setValue(value);
	}

	public TypedNumber(double value) {
		super(TypeEnum.DOUBLE);
		setValue(value);
	}

	public TypedNumber(boolean value) {
		super(TypeEnum.BOOL);
		setValue(value);
	}

	public long getValue() {
		return value;
	}

	public int getIntValue() {
		return (int) value;
	}

	public short getShortValue() {
		return (short) value;
	}

	public char getCharValue() {
		return (char) value;
	}

	public byte getByteValue() {
		return (byte) value;
	}

	public boolean getBooleanValue() {
		return value != 0;
	}

	public float getFloatValue() {
		return Float.intBitsToFloat((int) value);
	}

	public double getDoubleValue() {
		return Double.longBitsToDouble(value);
	}

	public void setValue(long value) {
		// If UINT64, then no need to reduce
		if (this.getType().getByteSize() != 8) {
			this.value = value & (((long) 1 << 8 * this.getType().getByteSize()) - 1);	
		} else {
			this.value = value;
		}
	}

	public void setValue(boolean value) {
		setValue(value ? 1 : 0);
	}

	public void setValue(float value) {
		setValue(Float.floatToIntBits(value));
	}

	public void setValue(double value) {
		setValue(Double.doubleToLongBits(value));
	}
	
	@Override
	public int getSize() {
		return getType().getByteSize();
	}

	@Override
	public String toString() {
		String format = "0x%%0%dX".formatted(getType().getByteSize() * 2);

		// Value is already formatted!
		return format.formatted(value);
	}
	
}
