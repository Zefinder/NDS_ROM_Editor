package pokemon.frame.panel.edition;

import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public abstract class FileUIEditionPanel extends JPanel implements Scrollable, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4235199305268073307L;

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return getParent() != null ? getParent().getSize().height > getPreferredSize().height : true;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return getParent() != null ? getParent().getSize().width > getPreferredSize().width : true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 8;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 8;
	}

}
