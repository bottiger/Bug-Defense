package com.game.towerdefense;

import android.graphics.Paint;

public class Color {
	
	public static Paint white() {
		return newPaint(255, 255, 255, 255);
	}
	
	public static Paint black() {
		return newPaint(255, 0, 0, 0);
	}
	
	static Paint healthLeftColor() {
		return newPaint(255, 255, 0, 0);
	}
	
	static Paint pathColor() {
		int pathWidth = (int)(40.0/TileView.mTileSize);
		return pathColor(pathWidth);
	}
	
	static Paint pathColor(float pathWidth) {
		return newPaint(139, 69, 19, 0, (int)pathWidth);
	}
	
	public static Paint canBuildColor() {
		return newPaint(50, 0, 0, 155);
	}
	
	public static Paint canNotBuildColor() {
		return newPaint(50, 155, 0, 0);
	}
	
	public static Paint towerRangeColor() {
		return newPaint(50, 0, 155, 0);
	}
	
	static Paint newPaint(int A, int R, int G, int B) {
		return newPaint(A, R, G, B, 1);
	}
	
	static Paint newPaint(int A, int R, int G, int B, int width) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(A, R, G, B);
		paint.setStrokeWidth(width);
		return paint;
	}

}
