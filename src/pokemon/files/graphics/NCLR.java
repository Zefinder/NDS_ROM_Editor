package pokemon.files.graphics;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.DSFileManager;
import pokemon.files.FileFormat;
import pokemon.files.SubSection;
import pokemon.files.graphics.GraphicResources.ColorBitDepth;
import pokemon.files.headers.GenericHeader;
import pokemon.files.headers.Header;
import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedArray;
import pokemon.files.types.TypedNumber;
import pokemon.logic.Palette;

public class NCLR extends FileFormat {

	private static final String NCLR_MAGIC = "RLCN";

	private TTLP ttlp;
	private PMCP pmcp;
	private boolean hasPMCP;

	public NCLR(ColorBitDepth bitDepth, int colorNumber, boolean hasPMCP) {
		super(NCLR_MAGIC, 0, hasPMCP ? 2 : 1);
		this.ttlp = new TTLP(bitDepth, colorNumber);
		this.pmcp = new PMCP(this.ttlp.getColors().length / 16);
		this.hasPMCP = hasPMCP;
		updateSize();
	}

	public NCLR(boolean hasPMCP) {
		this(ColorBitDepth.FOUR_BIT_DEPTH, 0x200, hasPMCP);
	}

	public NCLR() {
		this(false);
	}

	public ColorBitDepth getBitDepth() {
		return ttlp.getBitDepth();
	}

	public void setColor(Color color, int index) {
		this.ttlp.setColor(color, index);
	}

	public Palette createPalette() {
		return new Palette(getBitDepth().getBitDepthValue(), ttlp.getColors());
	}

	public void setPMCPSubsection(boolean set) {
		this.hasPMCP = set;
		super.setSubsectionNumber(set ? 2 : 1);
		updateSize();
	}

	@Override
	protected void updateSize() {
		int size = GenericHeader.DEFAULT_HEADER_SIZE + this.ttlp.getSize();
		if (hasPMCP) {
			size += this.pmcp.getSize();
		}

		super.setSize(size);
	}

	@Override
	protected void loadData(InputStream inStream) throws IOException {
		this.ttlp.load(inStream);
		// TODO Check PMCP
	}

	@Override
	protected void storeData(OutputStream outStream) throws IOException {
		this.ttlp.store(outStream);
	}

	private static class TTLP extends SubSection {

		private static final String TTLP_MAGIC = "TTLP";
		private static final int TTLP_DEFAULT_SIZE = Header.DEFAULT_HEADER_SIZE + 0x10;
		private static final TypedNumber PADDING = new TypedNumber(0, TypeEnum.UINT32);
		private static final TypedNumber COLORS_PER_PALETTE = new TypedNumber(0x10, TypeEnum.UINT32);

		private TypedNumber paletteBitDepth;
		private TypedNumber paletteDataSize;
		private TypedArray paletteData;

		/**
		 * Composed of 5 entries:
		 * <ul>
		 * <li>Palette bit depth (uint32)
		 * <li>Padding (uint32) = 0
		 * <li>Palette data size (uint32) = 0x200 if 8 bit depth or if 0x200 colors,
		 * 0x200 - colors otherwise
		 * <li>Colors per palette (uint32) = 0x10
		 * <li>Palette data (stored as NTFP) -> list of NTFP
		 * </ul>
		 * 
		 * @param bitDepth    The desired bit depth
		 * @param colorNumber The number of color. Will be aligned to 16
		 */
		public TTLP(ColorBitDepth bitDepth, int colorNumber) {
			super(TTLP_MAGIC, TTLP_DEFAULT_SIZE);

			this.paletteBitDepth = new TypedNumber(bitDepth.getBitDepthValue(), TypeEnum.UINT32);
			int size = 0x200;
			if (bitDepth == ColorBitDepth.FOUR_BIT_DEPTH) {
				// A palette must be a complete 16 color row
				if ((colorNumber & 0b1111) != 0) {
					colorNumber = (colorNumber & ~0b1111) + 0x10;
				}
				if (colorNumber != 0x200) {
					size -= colorNumber;
				}
			} else {
				colorNumber = 0x200;
			}
			this.paletteDataSize = new TypedNumber(size, TypeEnum.UINT32);
			this.paletteData = new TypedArray(TypeEnum.UINT16, colorNumber);
			super.setSize(TTLP_DEFAULT_SIZE + TypeEnum.UINT16.getByteSize() * colorNumber);
		}

		public void setColor(Color color, int index) {
			paletteData.setValue(GraphicResources.getBGR555FromRGB(color), index);
		}

		public Color[] getColors() {
			Color[] colors = new Color[paletteData.length()];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = GraphicResources.getRGBFromBGR555(paletteData.getIntValue(i));
			}
			return colors;
		}

		public ColorBitDepth getBitDepth() {
			return ColorBitDepth.fromBitDepth((int) paletteBitDepth.getValue());
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			DSFileManager.read(inStream, this.paletteBitDepth);
			DSFileManager.read(inStream, TypeEnum.UINT32); // Padding
			DSFileManager.read(inStream, this.paletteDataSize);
			DSFileManager.read(inStream, TypeEnum.UINT32); // Color per palette
		
			int size = this.paletteDataSize.getIntValue();
			if ((paletteBitDepth.getIntValue() & 0b11) == 3 && size != 0x200) {
				size = 0x200 - size;
			}

			this.paletteData = DSFileManager.read(inStream, TypeEnum.UINT16, size / 2);
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			DSFileManager.write(outStream, paletteBitDepth);
			DSFileManager.write(outStream, PADDING);
			DSFileManager.write(outStream, paletteDataSize);
			DSFileManager.write(outStream, COLORS_PER_PALETTE);
			DSFileManager.write(outStream, paletteData);
		}

	}

	private static class PMCP extends SubSection {

		private static final String PMCP_MAGIC = "PMCP";
		private static final int PMCP_DEFAULT_SIZE = 0x12;
		private static final TypedNumber CONSTANT1 = new TypedNumber(0xBEEF, TypeEnum.UINT16);
		private static final TypedNumber CONSTANT2 = new TypedNumber(0x08, TypeEnum.UINT32);

		private TypedNumber paletteNumber;
		private TypedArray paletteIds;

		public PMCP(int paletteNumber) {
			super(PMCP_MAGIC, PMCP_DEFAULT_SIZE);
			this.paletteNumber = new TypedNumber(paletteNumber, TypeEnum.UINT16);
			paletteIds = new TypedArray(TypeEnum.UINT16, paletteNumber);
			for (int i = 0; i < paletteNumber; i++) {
				paletteIds.setValue(i, i);
			}
			
			super.setSize(PMCP_DEFAULT_SIZE + TypeEnum.UINT16.getByteSize() * paletteNumber);
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			DSFileManager.read(inStream, this.paletteNumber);
			DSFileManager.read(inStream, TypeEnum.UINT16);
			DSFileManager.read(inStream, TypeEnum.UINT32);
			this.paletteIds = DSFileManager.read(inStream, TypeEnum.UINT16, paletteNumber.getIntValue());
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			DSFileManager.write(outStream, paletteNumber);
			DSFileManager.write(outStream, CONSTANT1);
			DSFileManager.write(outStream, CONSTANT2);
			DSFileManager.write(outStream, paletteIds);
		}

	}

}
