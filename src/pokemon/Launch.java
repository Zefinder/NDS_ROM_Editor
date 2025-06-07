package pokemon;

import java.io.IOException;

import pokemon.frame.MainFrame;
import pokemon.manager.SuperManager;

public class Launch {

	public static void main(String[] args) throws IOException {
		// Init managers
		SuperManager.getInstance().initManager();
		
//		// Choose the ROM folder
//		JFileChooser chooser = new JFileChooser();
//		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		int answer = chooser.showOpenDialog(null);
//		if (answer == JFileChooser.APPROVE_OPTION) {
//			File selectedFile = chooser.getSelectedFile();
//			MainFrame frame = new MainFrame(selectedFile.getName(), selectedFile.getPath());
//			frame.initFrame();
//		}
		
		MainFrame frame = new MainFrame("aaa", "C:\\Users\\Jakub\\Desktop\\Travail\\Pokemon\\planinum_desass\\root");
		frame.initFrame();
		
//		File f = new File("C:\\Users\\adric\\Desktop\\pokemon test\\test\\graphic\\record.narc");
//		InputStream inStream = new DataInputStream(new FileInputStream(f));
//		NARC narc = new NARC(f);
//		narc.load(inStream);
//		inStream.close();
		
//		NARC narc = new NARC(5, new String[][] { { "a", "c", "b" }, { "a1" }, {}, { "b1" }, {}, {} },
//				new String[][] { { "a.a" }, {}, {}, { "b.b" }, { "aa.a" }, { "bb1.b", "bb2.b" } },
//				new int[] { 10, 10, 10, 10, 10 });
	}

}
