package com.game.towerdefense;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class PlaceableTower {
	
	private Tile position;
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
	
	public Rect getRect() {
		return new Rect(x-size, y-size, x+size, y+size);
	}
	
	public Drawable getImage() {
		return this.image;
	}
	
	public void draw(Canvas canvas) {
		Paint rangeColor = new Paint();
		rangeColor.setAntiAlias(true);
		rangeColor.setARGB(50, 155, 0, 0);
		
		Paint canBuildColor = new Paint();
		canBuildColor.setAntiAlias(true);
		canBuildColor.setARGB(50, 0, 0, 155);
		
		image.setBounds(this.getRect());
		image.draw(canvas);
		canvas.drawCircle(x, y, getRange(), rangeColor);
		canvas.drawRect(getRect(), canBuildColor);
	}

}
