package pokemon.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.headers.Header;

public abstract class Section implements Loadable {

	protected Header header;

	public Section(Header header) {
		this.header = header;
	}

	protected void setSize(int size) {
		header.setSectionSize(size);
	}

	public int getSize() {
		return header.getSectionSize().getIntValue();
	}

	protected abstract void loadData(InputStream inStream) throws IOException;

	protected abstract void storeData(OutputStream outStream) throws IOException;

	@Override
	public void load(InputStream inStream) throws IOException {
		header.load(inStream);
		loadData(inStream);
	}

	@Override
	public void store(OutputStream outStream) throws IOException {
		header.store(outStream);
		storeData(outStream);
	}

}
