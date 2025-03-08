package pokemon.panel.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

import pokemon.event.Event;
import pokemon.event.EventListener;
import pokemon.event.EventManager;
import pokemon.event.palette.PaletteColorModifiedEvent;
import pokemon.logic.Palette;

public class PalettePanel extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8397164504164234274L;

	private static final int COLOR_SIZE = 20;
	private static final int OFFSET = 1;

	private Palette palette;
	private int pointedX;
	private int pointedY;

	public PalettePanel(Palette palette) {
		this.palette = palette;
		this.pointedX = -1;
		this.pointedY = -1;

		this.setPreferredSize(
				new Dimension((COLOR_SIZE + OFFSET) * 16 + 2 * OFFSET, (COLOR_SIZE + OFFSET) * 16 + 2 * OFFSET));
		this.setMaximumSize(this.getPreferredSize());
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		EventManager.getInstance().registerListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		// Set 1 pixel of offset to have black borders
		int x = OFFSET;
		int y = OFFSET;
		Color[][] colors = this.palette.getPalettes();
		for (Color[] line : colors) {
			for (Color color : line) {
				g2d.setColor(color);
				g2d.fillRect(x, y, COLOR_SIZE, COLOR_SIZE);
				x += COLOR_SIZE + OFFSET;
			}

			x = OFFSET;
			y += COLOR_SIZE + OFFSET;
		}

		// Highlight rectangle
		if (pointedX != -1 && pointedY != -1 && pointedX < (COLOR_SIZE + OFFSET) * 16
				&& pointedY < (COLOR_SIZE + OFFSET) * 16) {
			g2d.setColor(Color.lightGray);
			g2d.drawRect(pointedX, pointedY, COLOR_SIZE + 2 * OFFSET, COLOR_SIZE + 2 * OFFSET);
		}
	}

	@EventListener
	public void onPaletteColorChanged(PaletteColorModifiedEvent event) {
		palette.setColorInPalette(event.getPaletteNumber(), event.getIndex(), event.getNewColor());
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int selectedPalette = pointedY / (COLOR_SIZE + OFFSET);
			int selectedIndex = pointedX / (COLOR_SIZE + OFFSET);
			
			if (selectedPalette < 16 && selectedIndex < 16) {				
				Color oldColor = palette.getColorInPalette(selectedPalette, selectedIndex);
				Color newColor = JColorChooser.showDialog(null, "Change color", oldColor);
				
				if (newColor != null) {
					Event colorChangedEvent = new PaletteColorModifiedEvent(selectedPalette, selectedIndex, oldColor,
							newColor);
					EventManager.getInstance().throwEvent(colorChangedEvent);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		pointedX = -1;
		pointedY = -1;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Gets the x and y position of the rectangle to draw
		int x = e.getX();
		int y = e.getY();

		// Highlight rectangles take the two offsets into account
		// Do as if a rectangle had a size of COLOR_SIZE + OFFSET
		// Rescale to beginning of rectangle
		pointedX = (x / (COLOR_SIZE + OFFSET)) * (COLOR_SIZE + OFFSET);
		pointedY = (y / (COLOR_SIZE + OFFSET)) * (COLOR_SIZE + OFFSET);
		repaint();
	}
}
