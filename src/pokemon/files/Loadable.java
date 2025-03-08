package pokemon.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Loadable {
	
	void load(InputStream inStream) throws IOException;
	
	void store(OutputStream outStream) throws IOException;

}
