package com.game.towerdefense.ui;

import com.game.towerdefense.Bank;
import com.game.towerdefense.Color;
import com.game.towerdefense.GameState;
import com.game.towerdefense.R;
import com.game.towerdefense.TowerDefenseView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class UIController {

	private static Rect[] mTowerButtons = new Rect[3];
	
	private static Paint foregroundColor = Color.white();
	private static Paint backgroundColor = Color.black();
	
	private static int rightMarginTopBar = 0;
	private static int topBarHeight = 0;
	private static int itemPadding = 5;

	public static void drawMenuBox(TowerDefenseView view, Canvas canvas,
			Drawable[] icons) {

		int mWidth = view.getWidth();
		int mHeight = view.getHeight();

		int barHeight = view.getPercentOfHeight(12);
		int imageSize = view.getPercentOfHeight(5); // 6
		int bottomPadding = view.getPercentOfHeight(7);
		int textPadding = view.getPercentOfHeight(3);

		Paint backgroundColor = Color.black();
		Paint foregroundColor = Color.white();

		canvas.drawRect(0, view.getHeight(), view.getWidth(), view.getHeight()
				- barHeight, backgroundColor);

		mTowerButtons[0] = new Rect(mWidth / 8 - imageSize, mHeight - bottomPadding
				- imageSize, mWidth / 8 + imageSize, mHeight - bottomPadding
				+ imageSize);
		mTowerButtons[1] = new Rect(3 * mWidth / 8 - imageSize, mHeight - bottomPadding
				- imageSize, 3 * mWidth / 8 + imageSize, mHeight
				- bottomPadding + imageSize);
		mTowerButtons[2] = new Rect(5 * mWidth / 8 - imageSize, mHeight - bottomPadding
				- imageSize, 5 * mWidth / 8 + imageSize, mHeight
				- bottomPadding + imageSize);

		// mTowerImage mTower2Image mTower3Image
		UIController.drawTower(canvas, icons[0], foregroundColor, mTowerButtons[0]);
		UIController.drawTower(canvas, icons[1], foregroundColor, mTowerButtons[1]);
		UIController.drawTower(canvas, icons[2], foregroundColor, mTowerButtons[2]);

		// canvas.drawText("Escaped creeps: " + mLives, 6 * mWidth / 8,
		// mHeight - textPadding, foregroundColor);
		// canvas.drawText("Bank: " + mMoney.getAmount(), 6 * mWidth / 8,
		// mHeight - 2 * textPadding, foregroundColor);
		// canvas.drawText("FPS: " + mFPS, 6 * mWidth / 8, mHeight - 3
		// * textPadding, foregroundColor);
	}

	public static void drawTopBox(TowerDefenseView view, Canvas canvas) {

		int mWidth = view.getWidth();
		int mHeight = view.getHeight();
		int textPadding = view.getPercentOfHeight(3);

		topBarHeight = view.getPercentOfHeight(4);
		rightMarginTopBar = mWidth;

		canvas.drawRect(0, 0, view.getWidth(), topBarHeight, backgroundColor);

		drawTopImage(canvas, view.getResources().getDrawable(R.drawable.heart), GameState.getLives());
		GameState.getBank();
		drawTopImage(canvas, view.getResources().getDrawable(R.drawable.dollar), Bank.getAmount());
		//canvas.drawText("Escaped creeps: ", 6 * mWidth / 8, textPadding,
		//		foregroundColor);

	}
	
	public static Rect getTowerButton(int towerNumber) {
		return mTowerButtons[towerNumber];
	}
	
	private static void drawTopImage(Canvas canvas, Drawable image, String text) {
		foregroundColor.setTextSize(topBarHeight);
		int textWidth = (int)foregroundColor.measureText(text);
		rightMarginTopBar = rightMarginTopBar-textWidth;
		
		Rect imageRect = new Rect(rightMarginTopBar-topBarHeight, 0, rightMarginTopBar, topBarHeight);
		
		canvas.drawText(text, rightMarginTopBar, imageRect.bottom, foregroundColor);
		
		image.setBounds(imageRect);
		image.draw(canvas);

		rightMarginTopBar = imageRect.left;
		addTopBarPadding();
	}
	
	private static void drawTopImage(Canvas canvas, Drawable image, int text) {
		drawTopImage(canvas, image, new Integer(text).toString());
	}
	
	private static void drawTower(Canvas canvas, Drawable image, Paint color,
			Rect r) {
		int imageSize = 20;

		image.setBounds(r);
		image.draw(canvas);
		canvas.drawText("Price: " + 10, r.centerX() - 10, r.centerY()
				+ imageSize + 5, color);
	}
	
	private static void addTopBarPadding() {
		rightMarginTopBar -= itemPadding;
	}

}
