package pokemon.panel.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import pokemon.event.EventListener;
import pokemon.event.EventManager;
import pokemon.event.palette.PaletteColorModifiedEvent;
import pokemon.event.palette.PaletteSelectedEvent;
import pokemon.event.tile.TilePixelModifiedEvent;
import pokemon.event.ui.TilePropertiesChangedEvent;
import pokemon.logic.Palette;
import pokemon.logic.Tile;
import pokemon.panel.ui.EditionPanel;

public class TilePanel extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1381704300700120039L;

	private EditionPanel editionPanel;
	private String tileName;
	private Tile[] tiles;
	private Palette palette;

	private int tilesX;
	private int tilesY;

	private int zoom;
	private int selectedPalette;

	private boolean doDrawBackground;
	private boolean doDrawTileGrid;
	private boolean doDrawPixelGrid;

	private int pointedX;
	private int pointedY;

	public TilePanel(EditionPanel editionPanel, String tileName, Tile[] tiles, int tileX, int tileY, Palette palette) {
		this.editionPanel = editionPanel;
		this.tileName = tileName;
		this.palette = palette;
		this.tilesX = tileX;
		this.tilesY = tileY;
		this.tiles = tiles;

		this.zoom = 3;
		this.selectedPalette = 0;

		this.doDrawBackground = false;
		this.doDrawTileGrid = true;
		this.doDrawPixelGrid = false;

		this.pointedX = -1;
		this.pointedY = -1;

		this.setPreferredSize(new Dimension(8 * tilesX * zoom, 8 * tilesY * zoom));

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		EventManager.getInstance().registerListener(this);
	}

	private void updateSize() {
		this.setPreferredSize(new Dimension(8 * tilesX * zoom, 8 * tilesY * zoom));
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
		updateSize();
		repaint();
	}

	private void resizeTiles() {
		// Check if need to resize array
		if (tiles.length < tilesX * tilesY) {
			Tile[] newTiles = new Tile[tilesX * tilesY];

			// Copy old tiles
			for (int i = 0; i < tiles.length; i++) {
				newTiles[i] = tiles[i];
			}

			// Create new empty tiles
			for (int i = tiles.length; i < newTiles.length; i++) {
				newTiles[i] = new Tile();
			}
		}

		updateSize();
		revalidate();
	}
	
	public void setPaletteIndex(int selectedIndex) {
		selectedPalette = selectedIndex;
		repaint();
	}

	public void setTilesX(int tilesX) {
		this.tilesX = tilesX;
		resizeTiles();
		repaint();
	}

	public void setTilesY(int tilesY) {
		this.tilesY = tilesY;
		resizeTiles();
		repaint();
	}

	public void setDoDrawBackground(boolean doDrawBackground) {
		this.doDrawBackground = doDrawBackground;
		repaint();
	}

	public void setDoDrawTileGrid(boolean doDrawTileGrid) {
		this.doDrawTileGrid = doDrawTileGrid;
		repaint();
	}

	public void setDoDrawPixelGrid(boolean doDrawPixelGrid) {
		this.doDrawPixelGrid = doDrawPixelGrid;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		int tileIndex = 0;
		for (int y = 0; y < tilesY; y++) {
			for (int x = 0; x < tilesX; x++) {
				// There will always be enough tiles!
				Tile tile = tiles[tileIndex++];
				int baseX = x * Tile.TILE_SIZE * zoom;
				int baseY = y * Tile.TILE_SIZE * zoom;

				for (int xIndex = 0; xIndex < Tile.TILE_SIZE; xIndex++) {
					for (int yIndex = 0; yIndex < Tile.TILE_SIZE; yIndex++) {
						// Even with 8 bits depth, selected palette is set (to 0)
						int paletteIndex = tile.getData(xIndex, yIndex);

						Color color;
						if (paletteIndex == 0 && !doDrawBackground) {
							color = Color.white;
						} else {
							color = palette.getColorInPalette(selectedPalette, paletteIndex);
						}
						g2d.setColor(color);
						g2d.fillRect(baseX + xIndex * zoom, baseY + yIndex * zoom, zoom, zoom);
					}
				}
			}
		}

		// Draw pixel grid when asked (not smart if zoom is 1)
		// Draw before to prioritize tile grid
		if (doDrawPixelGrid) {
			g2d.setColor(Color.darkGray);

			// Draw horizontal lines
			for (int i = 0; i < tilesY * 8; i++) {
				g2d.drawLine(0, zoom * i, 8 * tilesX * zoom, zoom * i);
			}

			// Draw vertical lines
			for (int i = 0; i < tilesX * 8; i++) {
				g2d.drawLine(zoom * i, 0, zoom * i, 8 * tilesY * zoom);
			}
		}

		// Draw tile grid when asked
		if (doDrawTileGrid) {
			g2d.setColor(Color.lightGray);

			// Draw horizontal lines
			for (int i = 0; i < tilesY; i++) {
				g2d.drawLine(0, 8 * zoom * i, 8 * tilesX * zoom, 8 * zoom * i);
			}

			// Draw vertical lines
			for (int i = 0; i < tilesX; i++) {
				g2d.drawLine(8 * zoom * i, 0, 8 * zoom * i, 8 * tilesY * zoom);
			}
		}

		// Draw selection rectangle
		if (pointedX != -1 && pointedY != -1 && pointedX < 8 * zoom * tilesX && pointedY < 8 * zoom * tilesY) {
			g2d.setColor(Color.white);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawRect(pointedX, pointedY, 8 * zoom, 8 * zoom);
		}
	}

	@EventListener
	public void onPaletteColorChanged(PaletteColorModifiedEvent event) {
		repaint();
	}

	@EventListener
	public void onTilePixelMidified(TilePixelModifiedEvent event) {
//		Tile modifiedTile = tiles[event.getTileIndex()];
//		modifiedTile.setData(event.getxPixelIndex(), event.getyPixelIndex(), event.getNewColorIndex());
//		repaint();
	}

	@EventListener
	public void onTilePropertiesChanged(TilePropertiesChangedEvent event) {
		// Only change when it's the same name
		if (event.getTileName().equals(tileName)) {
			switch (event.getProperty()) {
			case ZOOM:
				setZoom(event.getValue());
				break;

			case SELECTED_PALETTE:
				setPaletteIndex(event.getValue());
				break;

			case TILE_X:
				setTilesX(event.getValue());
				break;

			case TILE_Y:
				setTilesY(event.getValue());
				break;

			case SHOW_TILE_GRID:
				setDoDrawTileGrid(event.getValue() == 1);
				break;

			case SHOW_PIXEL_GRID:
				setDoDrawPixelGrid(event.getValue() == 1);
				break;

			case TRANSPARENT_BG:
				setDoDrawBackground(event.getValue() == 1);
				break;
			}
		}
	}

	@EventListener
	public void onPaletteSelected(PaletteSelectedEvent event) {
		this.palette = editionPanel.getCurrentPalette();
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int xSelectedTile = pointedX / (8 * zoom);
		int ySelectedTile = pointedY / (8 * zoom);

		if (xSelectedTile < tilesX && ySelectedTile < tilesY) {
//			int selectedTile = xSelectedTile + tilesX * ySelectedTile;
//			Event tileSelectedEvent = new TileSelectedEvent(selectedTile, tiles[selectedTile].getTileData());
//			EventManager.getInstance().throwEvent(tileSelectedEvent);
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
		pointedX = (x / (8 * zoom)) * 8 * zoom;
		pointedY = (y / (8 * zoom)) * 8 * zoom;
		repaint();
	}

}
