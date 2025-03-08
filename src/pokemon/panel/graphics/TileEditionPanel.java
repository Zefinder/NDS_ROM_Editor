package pokemon.panel.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pokemon.event.Event;
import pokemon.event.EventListener;
import pokemon.event.EventManager;
import pokemon.event.palette.PaletteColorModifiedEvent;
import pokemon.event.tile.TileEditSelectedEvent;
import pokemon.event.tile.TilePixelModifiedEvent;
import pokemon.logic.Palette;

public class TileEditionPanel extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1072743715101911646L;
	private static final int PIXEL_NUMBER = 8;
	private static final int PIXEL_SIZE = 40;

	private Palette palette;
	private int selectedPalette;

	private int selectedTile;
	private int[][] tileData;

	private int pointedX;
	private int pointedY;

	public TileEditionPanel(Palette palette, int selectedPalette) {
		this.palette = palette;
		this.selectedPalette = selectedPalette;

		this.selectedTile = -1;
		this.tileData = null;

		this.pointedX = -1;
		this.pointedY = -1;

		this.setPreferredSize(new Dimension(PIXEL_NUMBER * PIXEL_SIZE, PIXEL_NUMBER * PIXEL_SIZE));

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		EventManager.getInstance().registerListener(this);
	}

	private Color getContrastColor(Color color) {
		double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
		return y >= 128 ? Color.black : Color.white;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Font font = new Font("Arial", Font.PLAIN, 20);
		FontMetrics metrics = g.getFontMetrics(font);
		g2d.setFont(font);

		// Draw pixels
		if (selectedTile != -1) {
			Color[] colors = palette.getPalettes()[selectedPalette];

			for (int x = 0; x < PIXEL_NUMBER; x++) {
				for (int y = 0; y < PIXEL_NUMBER; y++) {
					Color color = colors[tileData[x][y]];
					g2d.setColor(color);
					g2d.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);

					// Draw palette index in it (in negative color)
					g2d.setColor(getContrastColor(color));
					String text = "%d".formatted(tileData[x][y]);
					g2d.drawString(text, x * PIXEL_SIZE + (PIXEL_SIZE - metrics.stringWidth(text)) / 2,
							y * PIXEL_SIZE + metrics.getAscent() + (PIXEL_SIZE - metrics.getHeight()) / 2);
				}
			}
		} else {
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, PIXEL_NUMBER * PIXEL_SIZE, PIXEL_NUMBER * PIXEL_SIZE);
		}

		// Draw grid
		g2d.setColor(Color.darkGray);
		for (int i = 1; i < PIXEL_NUMBER; i++) {
			g2d.drawLine(0, i * PIXEL_SIZE, PIXEL_NUMBER * PIXEL_SIZE, i * PIXEL_SIZE);
			g2d.drawLine(i * PIXEL_SIZE, 0, i * PIXEL_SIZE, PIXEL_NUMBER * PIXEL_SIZE);
		}

		// Draw selection
		if (pointedX != -1 && pointedY != -1 && pointedX < PIXEL_NUMBER * PIXEL_SIZE
				&& pointedY < PIXEL_NUMBER * PIXEL_SIZE) {
			g2d.setColor(Color.white);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawRect(pointedX, pointedY, PIXEL_SIZE, PIXEL_SIZE);
		}

	}

	@EventListener
	public void onPaletteColorChanged(PaletteColorModifiedEvent event) {
		repaint();
	}

	@EventListener
	public void onSelectedTile(TileEditSelectedEvent event) {
		selectedTile = event.getTileIndex();
		tileData = event.getTileData();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (selectedTile != -1) {
			int xSelectedPixel = pointedX / PIXEL_SIZE;
			int ySelectedPixel = pointedY / PIXEL_SIZE;

			if (xSelectedPixel < PIXEL_NUMBER && ySelectedPixel < PIXEL_NUMBER) {
				int oldIndex = tileData[xSelectedPixel][ySelectedPixel];
				String newIndexString = JOptionPane.showInputDialog(this, "Enter a new palette index", oldIndex);
				if (newIndexString == null) {
					// Happens when cancelled
					return;
				}
				
				int newIndex;
				try {
					newIndex = Integer.parseInt(newIndexString);
				} catch (NumberFormatException exception) {
					JOptionPane.showMessageDialog(null, "The index must be an integer", "Error!",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (newIndex >= palette.getPalettes()[selectedPalette].length) {
					JOptionPane.showMessageDialog(null, "The index must be a valid index within the palette", "Error!",
							JOptionPane.ERROR_MESSAGE);
				} else {
					Event pixelModifiedEvent = new TilePixelModifiedEvent(selectedTile, xSelectedPixel, ySelectedPixel,
							oldIndex, newIndex);
					EventManager.getInstance().throwEvent(pixelModifiedEvent);
					repaint();
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

		// Rescale to beginning of tile
		pointedX = (x / (PIXEL_SIZE)) * PIXEL_SIZE;
		pointedY = (y / (PIXEL_SIZE)) * PIXEL_SIZE;
		repaint();
	}

}
