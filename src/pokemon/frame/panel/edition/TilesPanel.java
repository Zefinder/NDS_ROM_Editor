package pokemon.frame.panel.edition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import pokemon.event.EventListener;
import pokemon.event.palette.PaletteColorModifiedEvent;
import pokemon.event.tile.TilePixelModifiedEvent;
import pokemon.event.tile.TilesPaletteColorDepthChangedEvent;
import pokemon.event.tile.TilesPaletteIndexChangedEvent;
import pokemon.event.tile.TilesPaletteSelectedEvent;
import pokemon.event.tile.TilesPerColumnChangedEvent;
import pokemon.event.tile.TilesPerRowChangedEvent;
import pokemon.event.tile.TilesPixelGridChangedEvent;
import pokemon.event.tile.TilesTileGridChangedEvent;
import pokemon.event.tile.TilesTransparentBackgroundChangedEvent;
import pokemon.event.tile.TilesZoomChangedEvent;
import pokemon.files.graphics.GraphicResources.ColorBitDepth;
import pokemon.logic.Palette;
import pokemon.logic.Tile;
import pokemon.logic.Tiles;
import pokemon.manager.EventManager;
import pokemon.manager.OpenedResourceManager;

public class TilesPanel extends FileUIEditionPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1381704300700120039L;

	private String tileName;
	private Tiles tiles;

	private int tilesX;
	private int tilesY;

	private Palette palette;
	private int zoom;
	private int selectedPalette;
	private ColorBitDepth bitDepth;

	private boolean doDrawBackground;
	private boolean doDrawTileGrid;
	private boolean doDrawPixelGrid;

	private int pointedX;
	private int pointedY;

	public TilesPanel(String tileName, Tiles tiles, int tileX, int tileY) {
		this.tileName = tileName;
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

		updateSize();

		// Tries to get the palette with the tile name, or default
		palette = OpenedResourceManager.getInstance().getPaletteOrAvailable(tileName);
		bitDepth = ColorBitDepth.fromBitDepth(palette.getBitDepth());

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
		this.getParent().revalidate();
		repaint();
	}

	public void setPaletteIndex(int selectedIndex) {
		selectedPalette = selectedIndex;
		repaint();
	}

	public void setPaletteColorBitDepth(ColorBitDepth bitDepth) {
		this.bitDepth = bitDepth;
	}

	public void setTilesX(int tilesX) {
		tiles.resizeXTiles(tilesX);
		this.tilesX = tilesX;
		repaint();
	}

	public void setTilesY(int tilesY) {
		tiles.resizeYTiles(tilesY);
		this.tilesY = tilesY;
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

		for (int y = 0; y < tilesY; y++) {
			for (int x = 0; x < tilesX; x++) {
				// There will always be enough tiles!
				Tile tile = tiles.getTile(x, y);
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
	public void onPaletteSelected(TilesPaletteSelectedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			this.palette = OpenedResourceManager.getInstance().getPalette(event.getPaletteName());
			setPaletteColorBitDepth(ColorBitDepth.fromBitDepth(palette.getBitDepth()));
			this.repaint();
		}
	}

	@EventListener
	public void onZoomChanged(TilesZoomChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setZoom(event.getZoom());
		}
	}

	@EventListener
	public void onPaletteIndexChanged(TilesPaletteIndexChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setPaletteIndex(event.getPaletteIndex());
		}
	}

	@EventListener
	public void onPaletteColorDepthChanged(TilesPaletteColorDepthChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setPaletteColorBitDepth(event.getColorBitDepth());
		}
	}

	@EventListener
	public void onTilesPerColumnChanged(TilesPerColumnChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setTilesX(event.getTileX());
		}
	}

	@EventListener
	public void onTilesPerRowChanged(TilesPerRowChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setTilesY(event.getTileY());
		}
	}

	@EventListener
	public void onShowTileGridChanged(TilesTileGridChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setDoDrawTileGrid(event.isShowTileGrid());
		}
	}

	@EventListener
	public void onShowPixelGridChanged(TilesPixelGridChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setDoDrawPixelGrid(event.isShowPixelGrid());
		}
	}

	@EventListener
	public void onShowTransparentBackgroundChanged(TilesTransparentBackgroundChangedEvent event) {
		if (this.tileName.equals(event.getTileName())) {
			setDoDrawBackground(!event.isShowTransparentBackground());
		}
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 8 * zoom;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 8 * zoom;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
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
