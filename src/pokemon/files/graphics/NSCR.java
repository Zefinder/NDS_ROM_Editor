package pokemon.files.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.DSFileManager;
import pokemon.files.FileFormat;
import pokemon.files.SubSection;
import pokemon.files.headers.GenericHeader;
import pokemon.files.headers.Header;
import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedArray;
import pokemon.files.types.TypedNumber;
import pokemon.logic.ScreenData;

public class NSCR extends FileFormat {

	private static final String NSCR_MAGIC = "RCSN";

	private NRCS nrcs;

	public NSCR(int screenWidth, int screenHeight, int[] screenData) {
		super(NSCR_MAGIC, 0, 1);
		this.nrcs = new NRCS(screenWidth, screenHeight, screenData);
		updateSize();
	}

	public NSCR() {
		this(0, 0, new int[0]);
	}

	public ScreenData[] createScreenData() {
		return nrcs.getScreenData();
	}

	public int getScreenWidth() {
		return nrcs.getScreenWidth();
	}

	public int getScreenHeight() {
		return nrcs.getSceenHeight();
	}

	@Override
	protected void updateSize() {
		super.setSize(GenericHeader.DEFAULT_HEADER_SIZE + nrcs.getSize());
	}

	@Override
	protected void loadData(InputStream inStream) throws IOException {
		nrcs.load(inStream);
	}

	@Override
	protected void storeData(OutputStream outStream) throws IOException {
		nrcs.store(outStream);
	}

	private static class NRCS extends SubSection {

		private static final String NRCS_MAGIC = "NRCS";
		private static final int NRCS_DEFAULT_SIZE = Header.DEFAULT_HEADER_SIZE + 0x14;
		private static final TypedNumber PADDING = new TypedNumber(0, TypeEnum.UINT32);

		private TypedNumber screenWidth;
		private TypedNumber screenHeight;
		private TypedNumber screenDataSize;
		private TypedArray screenData;

		public NRCS(int screenWidth, int screenHeight, int[] screenData) {
			super(NRCS_MAGIC, NRCS_DEFAULT_SIZE);
			this.screenWidth = new TypedNumber(screenWidth, TypeEnum.UINT16); // Stored in pixels!
			this.screenHeight = new TypedNumber(screenHeight, TypeEnum.UINT16); // Stored in pixels!
			this.screenDataSize = new TypedNumber(screenData.length * TypeEnum.UINT16.getByteSize(), TypeEnum.UINT32);
			this.screenData = new TypedArray(TypeEnum.UINT16, screenData.length);
			this.screenData.setValues(screenData);

			super.setSize(NRCS_DEFAULT_SIZE + TypeEnum.UINT16.getByteSize() * screenData.length);
		}

		public ScreenData[] getScreenData() {
			ScreenData[] data = new ScreenData[screenData.length()];
			int[] rawData = screenData.getIntValues();
			for (int i = 0; i < data.length; i++) {
				data[i] = new ScreenData(rawData[i]);
			}

			return data;
		}

		public int getScreenWidth() {
			return screenWidth.getIntValue() >> 3;
		}

		public int getSceenHeight() {
			return screenHeight.getIntValue() >> 3;
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			DSFileManager.read(inStream, screenWidth);
			DSFileManager.read(inStream, screenHeight);
			DSFileManager.read(inStream, TypeEnum.UINT32); // Padding
			DSFileManager.read(inStream, screenDataSize);

			int size = screenDataSize.getIntValue() >> (TypeEnum.UINT16.getByteSize() - 1);
			screenData = new TypedArray(TypeEnum.UINT16, size);
			DSFileManager.read(inStream, screenData);
			
			super.setSize(NRCS_DEFAULT_SIZE + TypeEnum.UINT16.getByteSize() * screenData.length());
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			DSFileManager.write(outStream, screenWidth);
			DSFileManager.write(outStream, screenHeight);
			DSFileManager.write(outStream, PADDING);
			DSFileManager.write(outStream, screenDataSize);
			DSFileManager.write(outStream, screenData);
		}

	}
}
