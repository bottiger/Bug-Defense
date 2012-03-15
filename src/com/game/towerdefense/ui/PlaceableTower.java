package com.game.towerdefense.ui;

import com.game.towerdefense.Color;
import com.game.towerdefense.Tile;
import com.game.towerdefense.TileMap;
import com.game.towerdefense.TileView;
import com.game.towerdefense.towers.Tower;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class PlaceableTower {

	private Tile position;
	private Tile[] tiles;
	private int size;
	private Drawable image;
	private int range;

	private int x;
	private int y;

	public PlaceableTower(Tile tile, int size, Drawable image, int range) {
		this.position = tile;
		this.size = size;
		this.image = image;
		this.range = range;

		this.x = this.position.getPixel().x;
		this.y = this.position.getPixel().y;
	}

	public Tile getTile() {
		return this.position;
	}

	public int getX() {
		return this.position.x;
	}

	public int getY() {
		return this.position.y;
	}

	public int getRange() {
		return this.range;
	}

	public Rect getRect(Tile tile) {
		return new Rect((int) (tile.getPixel().x - TileView.mTileSize),
				(int) (tile.getPixel().y - TileView.mTileSize), (int) (tile
						.getPixel().x + TileView.mTileSize), (int) (tile
						.getPixel().y + TileView.mTileSize));
	}

	public Rect getRect() {
		return new Rect((int) (position.getPixel().x - size
				* TileView.mTileSize), (int) (position.getPixel().y - size
				* TileView.mTileSize), (int) (position.getPixel().x + size
				* TileView.mTileSize), (int) (position.getPixel().y + size
				* TileView.mTileSize));
	}

	public Drawable getImage() {
		return this.image;
	}

	public void draw(Canvas canvas) {
		// image.setBounds(this.getRect(tileSize));
		// image.draw(canvas);
		canvas.drawCircle(x, y, getRange() * TileView.mTileSize, Color
				.towerRangeColor());

		// canvas.drawRect(getRect(TileView.mTileSize), Color.canBuildColor());
		drawTiles(canvas);
	}

	private void drawTiles(Canvas canvas) {
		// only test the four corners of th tower
		Tile c1 = TileMap.getTile(position.x + size, position.y + size,
				TileView.mTileSize);
		Tile c2 = TileMap.getTile(position.x + size, position.y - size,
				TileView.mTileSize);
		Tile c3 = TileMap.getTile(position.x - size, position.y + size,
				TileView.mTileSize);
		Tile c4 = TileMap.getTile(position.x - size, position.y - size,
				TileView.mTileSize);

		if (c1.isBlocked() || c2.isBlocked() || c3.isBlocked()
				|| c4.isBlocked()) {
			canvas.drawRect(getRect(), Color.canNotBuildColor());
			return;
		}

		canvas.drawRect(getRect(), Color.canBuildColor());

		// for (int x = position.x-size; x <= position.x+size; x++) {
		// for (int y = position.y-size; y <position.y+size; y++) {
		// Tile tile = TileMap.getTile(x, y, TileView.mTileSize);
		// if (tile.isBlocked())
		// canvas.drawRect(getRect(tile), Color.canNotBuildColor());
		// else
		// canvas.drawRect(getRect(tile), Color.canBuildColor());
		// }
		// }
	}

}
