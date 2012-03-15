package com.game.towerdefense.towers;



import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;

public class HeavyTower extends AbstractTower {
	
	private static final int DAMAGE = 90;
	private static final int DELAY = 100;
	private static final int RANGE = 100;
	private static final int PENETRATION = 50;
	private static final int PRICE = 20;
	
	private Drawable image;
	
	// position, damage, delay, range, penetration, price
	public HeavyTower(Tile position) {
		super(position, DAMAGE, DELAY, RANGE, PENETRATION, PRICE);
	}
	
	public int getImageID() {
		return R.drawable.creeps;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable img) {
		image = img;
		
	}

}
