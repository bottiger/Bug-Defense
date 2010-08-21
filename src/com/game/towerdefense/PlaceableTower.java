package com.game.towerdefense;

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
				(int) (tile.getPixel().y - TileView.mTileSize), 
				(int) (tile.getPixel().x + TileView.mTileSize),
				(int) (tile.getPixel().y + TileView.mTileSize));
	}

	public Drawable getImage() {
		return this.image;
	}

	public void draw(Canvas canvas) {
		//image.setBounds(this.getRect(tileSize));
		//image.draw(canvas);
		canvas.drawCircle(x, y, getRange()*TileView.mTileSize, Color.towerRangeColor());
		//canvas.drawRect(getRect(TileView.mTileSize), Color.canBuildColor());
		drawTiles(canvas);
	}
	
	private void drawTiles(Canvas canvas) {
		for (int x = position.x-size; x <= position.x+size; x++) {
			for (int y = position.y-size; y <position.y+size; y++) {
				Tile tile = TileMap.getTile(x, y, TileView.mTileSize);
				if (tile.isBlocked())
					canvas.drawRect(getRect(tile), Color.canNotBuildColor());
				else
					canvas.drawRect(getRect(tile), Color.canBuildColor());
			}
		}
	}

}
