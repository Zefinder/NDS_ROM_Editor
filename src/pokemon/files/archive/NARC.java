package pokemon.files.archive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pokemon.files.DSFileManager;
import pokemon.files.FileFormat;
import pokemon.files.FormatEnum;
import pokemon.files.SubSection;
import pokemon.files.headers.GenericHeader;
import pokemon.files.headers.Header;
import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedArray;
import pokemon.files.types.TypedNumber;
import pokemon.files.types.TypedVariable;

public class NARC extends FileFormat {

	private static final String NARC_MAGIC = "NARC";

	private File archiveFile;

	private BTAF btaf;
	private BTNF btnf;
	private GMIF gmif;

	public NARC(File archiveFile, boolean doWriteSubTables) {
		super(NARC_MAGIC, 0, 3);
		this.archiveFile = archiveFile;
		super.setByteOrder(0xFFFE);

		if (archiveFile.exists()) {
			// Extract mode!
			this.btaf = new BTAF(new File[0][]);
			this.btnf = new BTNF(new String[0][], new File[0][], doWriteSubTables);
			this.gmif = new GMIF(new File[0][], new long[0][]);

		} else {
			// Archive mode! Read all files and directories in BFS
			String archiveName = archiveFile.getName();
			String archiveNameWoutExtension = archiveName.substring(0, archiveName.length() - 5);
			File archiveDir = new File(archiveFile.getParent() + File.separator + archiveNameWoutExtension);

			// List directories and process queue
			List<File[]> files = new ArrayList<File[]>();
			List<String[]> directories = new ArrayList<String[]>();

			Queue<File> dirQueue = new LinkedList<File>();
			dirQueue.add(archiveDir);

			while (!dirQueue.isEmpty()) {
				File dir = dirQueue.poll();
				List<File> dirFiles = new ArrayList<File>();
				List<String> dirDirs = new ArrayList<String>();

				// Append files to lists
				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						dirFiles.add(file);
					} else {
						dirQueue.offer(file);
						dirDirs.add(file.getName());
					}
				}

				// Create arrays and add to final list
				File[] dirFilesArray = new File[dirFiles.size()];
				for (int i = 0; i < dirFiles.size(); i++) {
					dirFilesArray[i] = dirFiles.get(i);
				}
				files.add(dirFilesArray);

				String[] dirDirsArray = new String[dirDirs.size()];
				for (int i = 0; i < dirDirs.size(); i++) {
					dirDirsArray[i] = dirDirs.get(i);
				}
				directories.add(dirDirsArray);
			}

			// Create arrays
			File[][] filesArray = new File[files.size()][];
			for (int i = 0; i < files.size(); i++) {
				filesArray[i] = files.get(i);
			}

			String[][] directoriesArray = new String[directories.size()][];
			for (int i = 0; i < directories.size(); i++) {
				directoriesArray[i] = directories.get(i);
			}

			// Create BTAF, BTNF and GMIF
			this.btaf = new BTAF(filesArray);
			this.btnf = new BTNF(directoriesArray, filesArray, doWriteSubTables);
			this.gmif = new GMIF(filesArray, btaf.getAddresses());

			// Update size
			updateSize();
		}
	}

	public NARC(File archiveFile) {
		this(archiveFile, false);
	}

	public File getExtractDir() {
		return btnf.getExtractDir();
	}

	public void createArchive() throws IOException {
		OutputStream outStream = new DataOutputStream(new FileOutputStream(archiveFile));
		super.store(outStream);
	}

	@Override
	protected void updateSize() {
		super.setSize(GenericHeader.DEFAULT_HEADER_SIZE + btaf.getSize() + btnf.getSize() + gmif.getSize());
	}

	@Override
	protected void loadData(InputStream inStream) throws IOException {
		btaf.load(inStream);
		btnf.load(inStream);

		// Recreate GMIF sub-section, so it has the files
		gmif = new GMIF(
				btnf.getArchiveFilesNames(archiveFile.getParentFile(), archiveFile.getName(), btaf.getFileNumber()),
				btaf.getAddresses());
		gmif.load(inStream);
	}

	@Override
	protected void storeData(OutputStream outStream) throws IOException {
		btaf.store(outStream);
		btnf.store(outStream);
		gmif.store(outStream);
	}

	private static class BTAF extends SubSection {

		private static final String BTAF_MAGIC = "BTAF";
		private static final int BTAF_DEFAULT_SIZE = Header.DEFAULT_HEADER_SIZE + 0x4;
		private static final TypedNumber PADDING = new TypedNumber(0, TypeEnum.UINT16);

		private TypedNumber fileNumber;
		private TypedArray fileAddresses;

		public BTAF(File[][] fileNames) {
			super(BTAF_MAGIC, BTAF_DEFAULT_SIZE);

			// For each file fill the current offset
			List<Long> addressesList = new ArrayList<Long>();
			long currentOffset = 0;
			for (File[] row : fileNames) {
				for (File file : row) {
					// Compute end address and merge both start and end
					long endAddress = currentOffset + file.length();
					long fileAddresses = ((endAddress) << 32) | currentOffset;
					addressesList.add(fileAddresses);

					// Update offset
					currentOffset = endAddress;

					// File addresses are 4-bytes aligned
					long paddingNeeded = currentOffset & 0b11;
					if (paddingNeeded != 0) {
						currentOffset += 4 - paddingNeeded;
					}
				}
			}

			this.fileNumber = new TypedNumber(addressesList.size(), TypeEnum.UINT16);

			long[] fileAddresses = new long[addressesList.size()];
			for (int i = 0; i < addressesList.size(); i++) {
				fileAddresses[i] = addressesList.get(i);
			}

			this.fileAddresses = new TypedArray(fileAddresses);

			super.setSize(BTAF_DEFAULT_SIZE + fileAddresses.length * TypeEnum.UINT64.getByteSize());
		}

		public long[][] getAddresses() {
			long[][] addresses = new long[fileNumber.getIntValue()][2];

			int index = 0;
			for (long address : fileAddresses.getLongValues()) {
				// Low 32 bits is start, high 32 bits is end
				long start = address & 0xFFFFFFFFL;
				long end = (address >> 32) & 0xFFFFFFFFL;
				addresses[index][0] = start;
				addresses[index++][1] = end;
			}

			return addresses;
		}

		public int getFileNumber() {
			return fileNumber.getIntValue();
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			DSFileManager.read(inStream, fileNumber);
			DSFileManager.read(inStream, TypeEnum.UINT16); // Padding

			this.fileAddresses = new TypedArray(TypeEnum.UINT64, fileNumber.getIntValue());
			DSFileManager.read(inStream, fileAddresses);
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			DSFileManager.write(outStream, fileNumber);
			DSFileManager.write(outStream, PADDING);
			DSFileManager.write(outStream, fileAddresses);
		}

	}

	private static class BTNF extends SubSection {

		private static final String BTNF_MAGIC = "BTNF";
		private static final int BTNF_DEFAULT_SIZE = Header.DEFAULT_HEADER_SIZE;

		private TypedNumber[][] mainTables;
		private TypedVariable[][][] subTables;

		private File extractDir;

		/**
		 * <p>
		 * Directories and files in the archive. The first index contains the root
		 * directories. Sub-directories are indexed in arrival order.
		 * </p>
		 * 
		 * <p>
		 * We can take the following example:<br>
		 * <code>
		 * /<br>
		 * |<br>
		 * |_ a<br>
		 * ~~~|_ a1<br>
		 * |<br>
		 * |_ b<br>
		 * ~~~|_ b1<br>
		 * </code> The first dirNames index will contain the root directories, so "a"
		 * and "b". The second will contain "a" directories, the third will contain "b",
		 * then "a1" and finally "b1". The fifth and sixth exist but are empty.
		 * </p>
		 * 
		 * <p>
		 * Files are indexed in arrival order too, meaning that their sizes are indexed
		 * the same way they arrive.
		 * </p>
		 * 
		 * @param dirNames  Directory names
		 * @param fileSizes File sizes
		 */
		public BTNF(String[][] dirNames, File[][] fileNames, boolean doWriteSubTables) {
			super(BTNF_MAGIC, BTNF_DEFAULT_SIZE);
			int dirNumber = dirNames.length;

			if (dirNumber == 0) {
				return;
			}

			// First are all main tables
			this.mainTables = new TypedNumber[dirNumber][3];

			// Write the first one by hand, offset is 8 * number of directories unless there
			// is no sub-table...
			int firstOffset = doWriteSubTables ? 8 * dirNumber : 4;
			this.mainTables[0][0] = new TypedNumber(firstOffset, TypeEnum.UINT32);
			this.mainTables[0][1] = new TypedNumber(0, TypeEnum.UINT16);
			this.mainTables[0][2] = new TypedNumber(dirNumber, TypeEnum.UINT16);

			int currentFileIndex = fileNames[0].length;
			int currentDirIndex = 0;
			int currentParentDir = 0xF000;
			int nextDirCounter = dirNames[0].length; // if more than 1 dir, then root must have one
			for (int dirIndex = 1; dirIndex < dirNumber; dirIndex++) {
				// Do not forget to fill it later!
				this.mainTables[dirIndex][0] = new TypedNumber(0, TypeEnum.UINT32);
				this.mainTables[dirIndex][1] = new TypedNumber(currentFileIndex, TypeEnum.UINT16);
				this.mainTables[dirIndex][2] = new TypedNumber(currentParentDir, TypeEnum.UINT16);

				// Decrease dir counter and update if needed
				nextDirCounter--;
				// The last dir cannot be a counter nor its own sub-(sub-)directory
				while (dirIndex < dirNumber - 1 && currentDirIndex < dirIndex && nextDirCounter == 0) {
					currentParentDir++;
					nextDirCounter = dirNames[++currentDirIndex].length;
				}

				// Increment file index
				currentFileIndex += fileNames[dirIndex].length;
			}

			if (doWriteSubTables) {
				// Then sub-tables (must finish by 00)
				this.subTables = new TypedVariable[dirNumber][][];
				int dirId = 0xF001;
				int currentOffset = 8 * dirNumber;
				for (int dirIndex = 0; dirIndex < dirNumber; dirIndex++) {
					// If not the root directory, then assign offset
					if (dirIndex != 0) {
						this.mainTables[dirIndex][0].setValue(currentOffset);
					}

					int subDirNumber = dirNames[dirIndex].length;
					int subFilesNumber = fileNames[dirIndex].length;
					int entryNumber = subDirNumber + subFilesNumber;
					int entryIndex = 0;
					TypedVariable[][] dirSubTable = new TypedVariable[entryNumber][];

					// First directories (size, string, id)
					for (int i = 0; i < subDirNumber; i++) {
						TypedVariable[] subDirTable = new TypedVariable[3];
						String dirName = dirNames[dirIndex][i];
						subDirTable[0] = new TypedNumber(0x80 | dirName.length(), TypeEnum.UINT8);
						subDirTable[1] = new TypedArray(dirName);
						subDirTable[2] = new TypedNumber(dirId++, TypeEnum.UINT16);

						// Increment offset
						currentOffset += TypeEnum.UINT8.getByteSize() + dirName.length()
								+ TypeEnum.UINT16.getByteSize();

						// Add to entry dir
						dirSubTable[entryIndex++] = subDirTable;
					}

					// Then files
					for (int i = 0; i < subFilesNumber; i++) {
						TypedVariable[] subFileTable = new TypedVariable[2];
						String fileName = fileNames[dirIndex][i].getName();
						subFileTable[0] = new TypedNumber(fileName.length() & 0x7F, TypeEnum.UINT8);
						subFileTable[1] = new TypedArray(fileName);

						// Increment offset
						currentOffset += TypeEnum.UINT8.getByteSize() + fileName.length();

						// Add to entry dir
						dirSubTable[entryIndex++] = subFileTable;
					}

					// Add to subtables
					this.subTables[dirIndex] = dirSubTable;

					// Add 1 to offset for terminator byte
					currentOffset += 1;
				}

				// The current offset holds the size of the section...
				// The size MUST BE a multiple of 4, so add padding if needed
				int size = BTNF_DEFAULT_SIZE + currentOffset;
				if ((size & 0b11) != 0) {
					size += 4 - (size & 0b11);
				}
				super.setSize(size);
			} else {
				super.setSize(BTNF_DEFAULT_SIZE + 0x8 * dirNumber);
			}
		}

		public String[][] getArchiveFilesNames(File archivePath, String archiveName, int fileNumber) {
			int dirNumber = mainTables.length;
			String archiveNameWoutExt = archiveName.substring(0, archiveName.length() - 5);
			String[][] archiveFiles = new String[dirNumber][];

			extractDir = new File(archivePath.getAbsolutePath() + File.separator + archiveNameWoutExt);
			extractDir.mkdir();

			// If no sub-table, then name the files using the archive name
			if (subTables == null) {
				String fileFormat = "%s_%d";
				// If only one directory, then no need to work
				if (dirNumber == 1) {
					String[] paths = new String[fileNumber];
					for (int fileIndex = 0; fileIndex < fileNumber; fileIndex++) {
						paths[fileIndex] = extractDir.getAbsolutePath() + File.separator
								+ fileFormat.formatted(archiveNameWoutExt, fileIndex);
					}
					archiveFiles[0] = paths;

				} else {
					int fileOffset = dirNumber;
					for (int dirIndex = 0; dirIndex < dirNumber - 1; dirIndex++) {
						// Number of files in the directory is the difference of file index with the
						// next directory (except for the last one)
						int dirFileNumber = mainTables[dirIndex + 1][1].getIntValue()
								- mainTables[dirIndex][1].getIntValue();
						String[] paths = new String[dirFileNumber];
						for (int fileIndex = 0; fileIndex < dirFileNumber; fileIndex++) {
							paths[fileIndex] = extractDir.getAbsolutePath() + File.separator
									+ fileFormat.formatted(archiveNameWoutExt, fileOffset++);
						}

						archiveFiles[dirIndex] = paths;
					}

					// The last one is the difference with the number of files - 1
					int lastDirFileNumber = mainTables[dirNumber - 1][1].getIntValue() - fileNumber - 1;
					String[] paths = new String[lastDirFileNumber];

					for (int fileIndex = 0; fileIndex < lastDirFileNumber; fileIndex++) {
						paths[fileIndex] = archivePath.getAbsolutePath() + File.separator
								+ fileFormat.formatted(archiveName, fileOffset++);
					}
					archiveFiles[dirNumber - 1] = paths;
				}

			} else {
				// TODO
			}

			return archiveFiles;
		}

		public File getExtractDir() {
			return extractDir;
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			// First main table will say how many directories there is
			TypedNumber firstOffset = DSFileManager.read(inStream, TypeEnum.UINT32);
			TypedNumber firstFileId = DSFileManager.read(inStream, TypeEnum.UINT16);
			TypedNumber dirNumber = DSFileManager.read(inStream, TypeEnum.UINT16);

			this.mainTables = new TypedNumber[dirNumber.getIntValue()][3];
			mainTables[0][0] = firstOffset;
			mainTables[0][1] = firstFileId;
			mainTables[0][2] = dirNumber;

			for (int dirIndex = 1; dirIndex < dirNumber.getIntValue(); dirIndex++) {
				mainTables[dirIndex][0] = DSFileManager.read(inStream, TypeEnum.UINT32);
				mainTables[dirIndex][1] = DSFileManager.read(inStream, TypeEnum.UINT16);
				mainTables[dirIndex][2] = DSFileManager.read(inStream, TypeEnum.UINT16);
			}

			// There are 8 + 8 * dirNumber bytes used here, if section size then no
			// sub-table
			if (super.getSize() != BTNF_DEFAULT_SIZE + 8 * dirNumber.getIntValue()) {
				// Create sub-table
				this.subTables = new TypedVariable[dirNumber.getIntValue()][][];

				// Compute the first offset
				int offset = BTNF_DEFAULT_SIZE + 8 * dirNumber.getIntValue();

				for (int dirIndex = 0; dirIndex < dirNumber.getIntValue(); dirIndex++) {
					// Read until we reach the offset...
					for (int i = 0; i < mainTables[dirIndex][0].getIntValue() - offset; i++) {
						DSFileManager.read(inStream, TypeEnum.UINT8);
						offset++;
					}

					// Use a list because we don't know how many entries there are
					List<TypedVariable[]> entries = new ArrayList<TypedVariable[]>();

					// Read until the terminator is reached
					boolean terminated = false;
					do {
						TypedNumber typeByte = DSFileManager.read(inStream, TypeEnum.UINT8);
						int typeValue = typeByte.getIntValue();
						if (typeValue == 0) {
							terminated = true;
							offset += TypeEnum.UINT8.getByteSize();

						} else if (typeValue < 0x80) {
							// It is a file
							TypedVariable[] fileEntry = new TypedVariable[2];
							fileEntry[0] = typeByte;
							fileEntry[1] = DSFileManager.read(inStream, TypeEnum.UINT8, typeValue);
							offset += TypeEnum.UINT8.getByteSize() + typeValue;

							entries.add(fileEntry);

						} else if (typeValue > 0x80) {
							// It is a folder
							TypedVariable[] dirEntry = new TypedVariable[3];
							dirEntry[0] = typeByte;
							dirEntry[1] = DSFileManager.read(inStream, TypeEnum.UINT8, typeValue & 0b01111111);
							dirEntry[2] = DSFileManager.read(inStream, TypeEnum.UINT16);
							offset += TypeEnum.UINT8.getByteSize() + typeValue
									& 0b01111111 + TypeEnum.UINT16.getByteSize();

							entries.add(dirEntry);
						}
					} while (!terminated);

					TypedVariable[][] subTable = new TypedVariable[entries.size()][];
					for (int i = 0; i < entries.size(); i++) {
						subTable[i] = entries.get(i);
					}

					this.subTables[dirIndex] = subTable;
				}

				// Read the padding if the offset is lesser than the size
				for (int i = 0; i < getSize() - offset; i++) {
					DSFileManager.read(inStream, TypeEnum.UINT8);
				}
			}
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			int dirNumber = this.mainTables.length;

			// Write main tables first
			for (int dirIndex = 0; dirIndex < dirNumber; dirIndex++) {
				DSFileManager.write(outStream, mainTables[dirIndex][0]);
				DSFileManager.write(outStream, mainTables[dirIndex][1]);
				DSFileManager.write(outStream, mainTables[dirIndex][2]);
			}

			// Check if size is greater than only main tables
			if (getSize() > BTNF_DEFAULT_SIZE + 8 * dirNumber) {
				// Keep in mind the offset for possible padding
				int offset = Header.DEFAULT_HEADER_SIZE + 8 * dirNumber;

				for (int dirIndex = 0; dirIndex < dirNumber; dirIndex++) {
					TypedVariable[][] entries = this.subTables[dirIndex];
					for (int entryIndex = 0; entryIndex < entries.length; entryIndex++) {
						// Stop thinking, write every entry
						for (TypedVariable var : entries[entryIndex]) {
							if (var instanceof TypedNumber) {
								DSFileManager.write(outStream, (TypedNumber) var);
								offset += var.getSize();
							} else if (var instanceof TypedArray) {
								DSFileManager.write(outStream, (TypedArray) var);
								offset += var.getSize();
							}
						}
					}

					// Write the terminator byte for the directory
					DSFileManager.write(outStream, new TypedNumber(0, TypeEnum.UINT8));
					offset += TypeEnum.UINT8.getByteSize();
				}

				// Do not forget the padding (fill with FFh)
				for (int i = 0; i < getSize() - offset; i++) {
					DSFileManager.write(outStream, new TypedNumber(0xFF, TypeEnum.UINT8));
				}
			}
		}
	}

	private static class GMIF extends SubSection {

		private static final String GMIF_MAGIC = "GMIF";
		private static final int GMIF_DEFAULT_SIZE = Header.DEFAULT_HEADER_SIZE;

		private String[][] archivedFilesPath;
		private long[][] fileAddresses;

		public GMIF(String[][] archivedFilesPath, long[][] fileAddresses) {
			super(GMIF_MAGIC, GMIF_DEFAULT_SIZE);
			this.archivedFilesPath = archivedFilesPath;
			this.fileAddresses = fileAddresses;
			updateSize();
		}

		public GMIF(File[][] archivedFilesPath, long[][] fileAddresses) {
			super(GMIF_MAGIC, GMIF_DEFAULT_SIZE);
			this.archivedFilesPath = new String[archivedFilesPath.length][];

			int rowIndex = 0;
			for (File[] row : archivedFilesPath) {
				int fileNumber = archivedFilesPath[rowIndex].length;
				this.archivedFilesPath[rowIndex] = new String[fileNumber];

				for (int fileIndex = 0; fileIndex < fileNumber; fileIndex++) {
					this.archivedFilesPath[rowIndex][fileIndex] = row[fileIndex].getAbsolutePath();
				}
			}
			this.fileAddresses = fileAddresses;
			updateSize();
		}

		private void updateSize() {
			if (fileAddresses.length > 0) {
				long lastEndAddress = fileAddresses[fileAddresses.length - 1][1];
				long padding = lastEndAddress & 0b11;
				if (padding != 0) {
					padding = 4 - padding;
				}
				super.setSize((int) (GMIF_DEFAULT_SIZE + lastEndAddress + padding));
			}
		}

		@Override
		protected void loadData(InputStream inStream) throws IOException {
			long currentOffset = 0;
			int fileIndex = 0;
			// Write directly in the files
			for (String[] row : archivedFilesPath) {
				for (String filePath : row) {
					long startAddress = fileAddresses[fileIndex][0];
					long endAddress = fileAddresses[fileIndex][1];

					// Read padding
					if (currentOffset < startAddress) {
						int bufferSize = (int) (startAddress - currentOffset);
						DSFileManager.readBytes(inStream, new byte[bufferSize]);
						currentOffset += bufferSize;
					}

					// A file can be empty (why? idk)
					if (startAddress == endAddress) {
						// If empty, just create file...
						File file = new File("%s.bin".formatted(filePath));
						file.createNewFile();
					} else {
						// Read magic bytes (don't forget to add offset)
						byte[] magicBuffer = new byte[4];
						int magicRead = DSFileManager.readBytes(inStream, magicBuffer);
						boolean isSmallFile = magicRead != 4;
						String extension = "bin";

						// If the file is not "small", then there is an extension
						if (!isSmallFile) {
							String magic = new String(magicBuffer);
							extension = FormatEnum.getExtensionFromMagic(magic);
						}

						// Create file
						File file = new File("%s.%s".formatted(filePath, extension));
						file.createNewFile();

						// Ignore if file is empty
						DataOutputStream outStream = new DataOutputStream(new FileOutputStream(file, false));

						// Write file
						// Write magic bytes
						outStream.write(magicBuffer, 0, magicRead);
						currentOffset += magicRead;

						// If file is bigger than 4 bytes write the rest
						if (!isSmallFile) {
							byte[] buffer;

							// If bytes read is not the buffer size, then EOF reached
							int bytesRead = 0;
							do {
								int bufferSize = (int) (endAddress - currentOffset < 1024 ? endAddress - currentOffset
										: 1024);
								buffer = new byte[bufferSize];
								bytesRead = DSFileManager.readBytes(inStream, buffer);
								System.out.println("0x%08X".formatted(currentOffset));
								currentOffset += bytesRead;
								outStream.write(buffer, 0, bytesRead);
							} while (currentOffset != endAddress);
						}

						outStream.close();
					}

					// Increase file index
					System.out.println(fileIndex);
					fileIndex++;
				}
			}
		}

		@Override
		protected void storeData(OutputStream outStream) throws IOException {
			// Save current offset to compute padding
			long currentOffset = 0;

			// Just put the files into the stream
			int fileIndex = 0;
			for (String[] row : archivedFilesPath) {
				for (String file : row) {
					// If the start address is not the current offset, then add padding
					long startAddress = fileAddresses[fileIndex++][0];

					// Write padding if any
					while (currentOffset < startAddress) {
						DSFileManager.write(outStream, new TypedNumber(0xFF, TypeEnum.UINT8));
						currentOffset++;
					}

					byte[] buffer = new byte[1024];
					DataInputStream inStream = new DataInputStream(new FileInputStream(file));

					// If bytes read is not the buffer size, then EOF reached
					int bytesRead = 0;
					do {
						bytesRead = inStream.read(buffer);
						DSFileManager.writeBytes(outStream, buffer, bytesRead);
						currentOffset += bytesRead;
					} while (bytesRead == buffer.length);

					inStream.close();
				}
			}

			// Add final padding
			long padding = currentOffset & 0b11;
			if (padding != 0) {
				for (; padding < 4; padding++) {
					DSFileManager.write(outStream, new TypedNumber(0xFF, TypeEnum.UINT8));
				}
			}
		}

	}

}
