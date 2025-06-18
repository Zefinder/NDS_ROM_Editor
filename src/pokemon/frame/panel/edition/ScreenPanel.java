package pokemon.frame.panel.edition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import pokemon.event.EventListener;
import pokemon.event.palette.PaletteSelectedEvent;
import pokemon.logic.Palette;
import pokemon.logic.ScreenData;
import pokemon.logic.Tile;
import pokemon.manager.EventManager;
import pokemon.manager.OpenedResourceManager;

public class ScreenPanel extends FileUIEditionPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1003657286801521283L;

	private ScreenData[] screenData;
	private Palette palette;
	private Tile[] tiles;
	private int screenWidth;
	private int screenHeight;

	private int zoom;

	public ScreenPanel(ScreenData[] screenData, Palette palette, Tile[] tiles, int screenWidth, int screenHeight) {
		this.screenData = screenData;
		this.palette = palette;
		this.tiles = tiles;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		this.zoom = 3;
		updateSize();

		EventManager.getInstance().registerListener(this);
	}

	private void updateSize() {
		this.setPreferredSize(new Dimension(8 * screenWidth * zoom, 8 * screenHeight * zoom));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Draw screen data (will never go oob)
		int screenIndex = 0;
		for (int y = 0; y < screenHeight; y++) {
			for (int x = 0; x < screenWidth; x++) {
				ScreenData data = screenData[screenIndex++];
				int baseX = x * Tile.TILE_SIZE * zoom;
				int baseY = y * Tile.TILE_SIZE * zoom;

				Color[] colors = data.processTile(tiles, palette);
				int xIndex = 0;
				int yIndex = 0;
				for (Color color : colors) {
					g2d.setColor(color);
					g2d.fillRect(baseX + xIndex * zoom, baseY + yIndex * zoom, zoom, zoom);

					// x goes back to 0 when one line is over
					if (++xIndex == Tile.TILE_SIZE) {
						xIndex = 0;
						yIndex++;
					}
				}
			}
		}
	}

	@EventListener
	public void onPaletteSelected(PaletteSelectedEvent event) {
		this.palette = OpenedResourceManager.getInstance().getPalette(event.getPaletteName());
		this.repaint();
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
