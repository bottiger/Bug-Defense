package com.game.towerdefense;

import com.game.towerdefense.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class MenuBox {
	
//	private Canvas canvas;
//	
//	public MenuBox(Canvas canvas) {
//		this.canvas = canvas;
//	}
	
	public static void DrawInterfaceBox(TowerDefenseView view, Canvas canvas, int lives, Bank bank) {
		
		int width = view.getWidth();
		int height = view.getHeight();
		
		int barHeight = 100;

		Drawable tower1 = view.getContext().getResources().getDrawable(R.drawable.tower1);
		Drawable tower2 = view.getContext().getResources().getDrawable(R.drawable.tower2);
		Drawable tower3 = view.getContext().getResources().getDrawable(R.drawable.tower3);
		
		Paint backgroundColor = new Paint();
		backgroundColor.setAntiAlias(true);
		backgroundColor.setARGB(255, 0, 0, 0);
		
		Paint foregroundColor = new Paint();
		foregroundColor.setAntiAlias(true);
		foregroundColor.setARGB(255, 255, 255, 255);
		
		canvas.drawRect(0, 0, width, barHeight, backgroundColor);
		drawTower(canvas, tower1, foregroundColor, width/8, 50);
		drawTower(canvas, tower2, foregroundColor, 3*width/8, 50);
		drawTower(canvas, tower3, foregroundColor, 5*width/8, 50);

		canvas.drawText("Escaped creeps: " + lives, 6*width/8, 40, foregroundColor);
		canvas.drawText("Bank: " + bank.getAmount(), 6*width/8, 60, foregroundColor);
	}
	
	public static void drawTower(Canvas canvas, Drawable image, Paint color, int x, int y) {
		int imageSize = 20;
		
		image.setBounds(x-imageSize, y-imageSize, x+imageSize, y+imageSize);
		image.draw(canvas);
		canvas.drawText("Price: " + 10, x-10, y+imageSize+5, color);
	}

}
