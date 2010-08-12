package com.game.towerdefense;

/**
 * Simple class containing two integer values and a comparison function. There's
 * probably something I should use instead, but this was quick and easy to
 * build.
 * 
 */
public class Tile {

	// Tile coordinates
	public final int x;
	public final int y;
	
	//private int tileSize;
	private TowerDefenseView view;
	private boolean blocked = false;

	public Tile(int x, int y, TowerDefenseView view) {
		this.x = x;
		this.y = y;
		//this.tileSize = tileSize;
		this.view = view;
	}

	public boolean equals(Tile other) {
		if (x == other.x && y == other.y) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Tile: [" + x + "," + y + "]";
	}
	
	public Pixel getPixel() {
		float tileSize = view.mTileSize;
		return new Pixel((int)(x*tileSize+tileSize/2), (int)(y*tileSize+tileSize/2));
	}
	
	public boolean isBlocked() {
		return this.blocked;
	}
}
