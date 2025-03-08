package pokemon.files.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.DSFileManager;
import pokemon.files.FileFormat;
import pokemon.files.SubSection;
import pokemon.files.graphics.GraphicResources.ColorBitDepth;
import pokemon.files.headers.GenericHeader;
import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedArray;
import pokemon.files.types.TypedNumber;
import pokemon.logic.Tile;

public class NCGR extends FileFormat {

	private static final String NCGR_MAGIC = "RGCN";

	private RAHC rahc;
	private SOPC sopc;

	private boolean hasSOPC;

	public NCGR(int tileX, int tileY, ColorBitDepth colorBitDepth) {
		super(NCGR_MAGIC, 0, 1);
		rahc = new RAHC(tileX, tileY, colorBitDepth);
		sopc = new SOPC(tileX, tileY);
		hasSOPC = false;
		updateSize();
	}

	public NCGR() {
		this(0, 0, ColorBitDepth.FOUR_BIT_DEPTH);
	}

	public Tile[] createTiles() {
		return rahc.getTiles();
	}

	public int getTileX() {
		return rahc.getTileX();
	}

	public int getTileY() {
		return rahc.getTileY();
	}

	public ColorBitDepth getColorBitDepth() {
		return rahc.getColorBitDepth();
	}

	public void setSOPCSubsection(boolean set) {
		this.hasSOPC = set;
		super.setSubsectionNumber(set ? 2 : 1);
		updateSize();
	}

	@Override
	protected void updateSize() {
		int size = GenericHeader.DEFAULT_HEADER_SIZE + rahc.getSize();
		if (super.getSubsectionNumber() == 2) {
			size += sopc.getSize();
		}
		super.setSize(size);
	}

	@Override
	protected void loadData(InputStream inStream) throws IOException {
		rahc.load(inStream);
		if (super.getSubsectionNumber() == 2) {
			hasSOPC = true;
			sopc.load(inStream);
		}

		updateSize();
	}

	@Override
	protected void storeData(OutputStream outStream) throws IOException {
		rahc.store(outStream);
		if (hasSOPC) {
			sopc.store(outStream);
		}
	}

	private static class RAHC extends SubSection {

		private static final String RAHC_MAGIC = "RAHC";
		private static final int DEFAULT_RAHC_SIZE = 0x20;
		private static final TypedNumber CONSTANT1 = new TypedNumber(0x0, TypeEnum.UINT32);
		private static final TypedNumber CONSTANT2 = new TypedNumber(0x18, TypeEnum.UINT32);

		private TypedNumber tileY;
		private TypedNumber tileX;
		private TypedNumber colorBitDepth;
		private TypedNumber unknown1;
		private TypedNumber unknown2;
		private TypedNumber tileDataSizeInBytes;
		private TypedArray tileData;

		public RAHC(int tileX, int tileY, ColorBitDepth colorBitDepth) {
			super(RAHC_MAGIC, DEFAULT_RAHC_SIZE);
			this.tileY = new TypedNumber(tileY, TypeEnum.UINT16);
			this.tileX = new TypedNumber(tileX, TypeEnum.UINT16);
			this.colorBitDepth = new TypedNumber(colorBitDepth.getBitDepthValue(), TypeEnum.UINT32);
			this.unknown1 = new TypedNumber(0, TypeEnum.UINT16);
			this.unknown2 = new TypedNumber(0, TypeEnum.UINT16);
			int size = 64 * tileX * tileY * 1024;
			if (colorBitDepth == ColorBitDepth.FOUR_BIT_DEPTH) {
				size >>= 1;
			}
			this.tileDataSizeInBytes = new TypedNumber(size, TypeEnum.UINT32);
			this.tileData = new TypedArray(TypeEnum.UINT8, 64 * tileX * tileY); // Store each pixel

			super.setSize(DEFAULT_RAHC_SIZE + TypeEnum.UINT8.getByteSize() * 64 * tileX * tileY);
		}

		public Tile[] getTiles() {
			Tile[] tiles = new Tile[tileY.getIntValue() * tileX.getIntValue()];
			int tileX = this.tileX.getIntValue();
			int tileY = this.tileY.getIntValue();
			for (int indexX = 0; indexX < tileX; indexX++) {
				for (int indexY = 0; indexY < tileY; indexY++) {
					int[] tileData = new int[64];
					for (int i = 0; i < 64; i++) {
						tileData[i] = this.tileData.getIntValue(i + 64 * indexX + 64 * tileX * indexY);
					}
					tiles[indexY * tileX + indexX] = new Tile(tileData);
				}
			}

			return tiles;
		}

		public int getTileX() {
			return tileX.getIntValue();
		}

		public int getTileY() {
			return tileY.getIntValue();
		}

		public ColorBitDepth getColorBitDepth() {
			return ColorBitDepth.fromBitDepth(colorBitDepth.getIntValue());
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			DSFileManager.read(inStream, this.tileY);
			DSFileManager.read(inStream, this.tileX);
			DSFileManager.read(inStream, this.colorBitDepth);
			DSFileManager.read(inStream, this.unknown1);
			DSFileManager.read(inStream, this.unknown2);
			DSFileManager.read(inStream, TypeEnum.UINT32); // Padding
			DSFileManager.read(inStream, this.tileDataSizeInBytes);
			DSFileManager.read(inStream, TypeEnum.UINT32); // Offset?

			int size;
			if (tileX.getIntValue() == 0xFFFF || tileX.getIntValue() == 0xFFFF) {
				int totalPixels = tileDataSizeInBytes.getIntValue() << (4 - colorBitDepth.getIntValue());
				int tilePerRow = (int) Math.ceil(Math.sqrt(totalPixels)) >> 3;
				this.tileX.setValue(tilePerRow);
				this.tileY.setValue(tilePerRow);
			}
			size = 64 * tileX.getIntValue() * tileY.getIntValue();
			this.tileData = new TypedArray(TypeEnum.UINT8, size);

			// If 4 bits depth, each byte is 2 pixels
			if (colorBitDepth.getValue() == ColorBitDepth.FOUR_BIT_DEPTH.getBitDepthValue()) {
				TypedArray data = DSFileManager.read(inStream, TypeEnum.UINT8, tileDataSizeInBytes.getIntValue());
				int index = 0;
				for (int doublePixel : data.getIntValues()) {
					this.tileData.setValue(doublePixel & 0xF, index++);
					this.tileData.setValue((doublePixel >> 4) & 0xF, index++);
				}
			} else {
				DSFileManager.read(inStream, tileData);
			}
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			DSFileManager.write(outStream, this.tileY);
			DSFileManager.write(outStream, this.tileX);
			DSFileManager.write(outStream, this.colorBitDepth);
			DSFileManager.write(outStream, this.unknown1);
			DSFileManager.write(outStream, this.unknown2);
			DSFileManager.write(outStream, CONSTANT1);
			DSFileManager.write(outStream, this.tileDataSizeInBytes);
			DSFileManager.write(outStream, CONSTANT2);
		}

	}

	private static class SOPC extends SubSection {

		private static final String SOPC_MAGIC = "SOPC";
		private static final int SOPC_SIZE = 0x10;
		private static final TypedNumber ZERO = new TypedNumber(0, TypeEnum.UINT32);

		private TypedNumber tileX;
		private TypedNumber tileY;

		public SOPC(int tileX, int tileY) {
			super(SOPC_MAGIC, SOPC_SIZE);

			// Never create a SOPC subsection, just load and rewrite
			this.tileX = new TypedNumber(tileX, TypeEnum.UINT16);
			this.tileY = new TypedNumber(tileY, TypeEnum.UINT16);

			// Cannot change
			super.setSize(SOPC_SIZE);
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			DSFileManager.read(inStream, TypeEnum.UINT32);
			DSFileManager.read(inStream, tileX);
			DSFileManager.read(inStream, tileY);
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			DSFileManager.write(outStream, ZERO);
			DSFileManager.write(outStream, tileX);
			DSFileManager.write(outStream, tileY);
		}

	}

}
