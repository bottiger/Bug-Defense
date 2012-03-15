package com.game.towerdefense;

import com.game.towerdefense.towers.Tower;
import android.graphics.Rect;

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

	// private int tileSize;
	private boolean blocked = false;
	private Tower blockedBy = null;;

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
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
		return new Pixel(
				(int) (x * TileView.mTileSize + TileView.mTileSize / 2),
				(int) (y * TileView.mTileSize + TileView.mTileSize / 2));
	}

	public boolean isBlocked() {
		return this.blocked;
	}
	
	public boolean hasTower() {
		return (this.blocked == true) && (this.blockedBy != null);
	}
	
	public Tower getTower() {
		return this.blockedBy;
	}

	public void block(Tower tower) {
		this.blocked = true;
		this.blockedBy = tower;
	}
	
	public void block() {
		this.blocked = true;
	}
}
