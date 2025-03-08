package pokemon.files.types;

public class TypedArray extends TypedVariable {

	private TypedNumber[] values;

	public TypedArray(TypeEnum type, int length) {
		super(type);
		this.values = new TypedNumber[length];
		for (int i = 0; i < length; i++) {
			this.values[i] = new TypedNumber(0, type);
		}
	}

	public TypedArray(TypedNumber[] values, TypeEnum type) {
		super(type);
		this.values = values;
	}

	public TypedArray(long[] values, TypeEnum type) {
		this(type, values.length);
		setValues(values);
	}

	public TypedArray(long[] values) {
		this(TypeEnum.UINT64, values.length);
		setValues(values);
	}

	public TypedArray(int[] values) {
		this(TypeEnum.UINT32, values.length);
		setValues(values);
	}

	public TypedArray(short[] values) {
		this(TypeEnum.UINT16, values.length);
		setValues(values);
	}

	public TypedArray(char[] values) {
		this(TypeEnum.CHAR, values.length);
		setValues(values);
	}

	public TypedArray(byte[] values) {
		this(TypeEnum.UINT8, values.length);
		setValues(values);
	}

	public TypedArray(String str) {
		this(TypeEnum.UINT8, str.length());
		setValues(str.toCharArray());
	}

	public void setValue(TypedNumber value, int index) {
		this.values[index] = value;
	}

	public void setValue(long value, int index) {
		this.values[index] = new TypedNumber(value, this.getType());
	}

	public void setValue(int value, int index) {
		this.values[index] = new TypedNumber(value, this.getType());
	}

	public void setValue(short value, int index) {
		this.values[index] = new TypedNumber(value, this.getType());
	}

	public void setValue(char value, int index) {
		this.values[index] = new TypedNumber(value, this.getType());
	}

	public void setValue(byte value, int index) {
		this.values[index] = new TypedNumber(value, this.getType());
	}

	public void setValue(float value, int index) {
		this.values[index] = new TypedNumber(value);
	}

	public void setValue(double value, int index) {
		this.values[index] = new TypedNumber(value);
	}

	public void setValue(boolean value, int index) {
		this.values[index] = new TypedNumber(value);
	}

	public void setValues(TypedNumber[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(long[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(int[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(short[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(char[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(byte[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(float[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(double[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public void setValues(boolean[] values) {
		for (int i = 0; i < this.values.length; i++) {
			setValue(values[i], i);
		}
	}

	public TypedNumber getValue(int index) {
		return this.values[index];
	}

	public long getLongValue(int index) {
		return this.values[index].getValue();
	}

	public int getIntValue(int index) {
		return this.values[index].getIntValue();
	}

	public short getShortValue(int index) {
		return this.values[index].getShortValue();
	}

	public char getCharValue(int index) {
		return this.values[index].getCharValue();
	}

	public byte getByteValue(int index) {
		return this.values[index].getByteValue();
	}

	public float getFloatValue(int index) {
		return this.values[index].getFloatValue();
	}

	public double getDoubleValue(int index) {
		return this.values[index].getDoubleValue();
	}

	public boolean getBooleanValue(int index) {
		return this.values[index].getBooleanValue();
	}

	public TypedNumber[] getValues() {
		return values;
	}

	public long[] getLongValues() {
		long[] values = new long[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getLongValue(i);
		}

		return values;
	}

	public int[] getIntValues() {
		int[] values = new int[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getIntValue(i);
		}

		return values;
	}

	public short[] getShortValues() {
		short[] values = new short[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getShortValue(i);
		}

		return values;
	}

	public char[] getCharValues() {
		char[] values = new char[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getCharValue(i);
		}

		return values;
	}

	public byte[] getByteValues() {
		byte[] values = new byte[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getByteValue(i);
		}

		return values;
	}

	public float[] getFloatValues() {
		float[] values = new float[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getFloatValue(i);
		}

		return values;
	}

	public double[] getDoubleValues() {
		double[] values = new double[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getDoubleValue(i);
		}

		return values;
	}

	public boolean[] getBooleanValues() {
		boolean[] values = new boolean[this.values.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = getBooleanValue(i);
		}

		return values;
	}

	public String getStringValue() {
		return new String(getCharValues());
	}

	public int length() {
		return this.values.length;
	}

	@Override
	public int getSize() {
		return length() * getType().getByteSize();
	}

}
