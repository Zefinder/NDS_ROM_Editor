package pokemon.files;

import java.io.IOException;
import java.io.OutputStream;

public interface Exportable {
	
	void export(OutputStream outStream) throws IOException;
	
}
