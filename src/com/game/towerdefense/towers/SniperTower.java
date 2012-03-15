package com.game.towerdefense.towers;



import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;

public class SniperTower extends AbstractTower {
	
	private static final int DAMAGE = 10;
	private static final int DELAY = 20;
	private static final int RANGE = 200;
	private static final int PENETRATION = 40;
	private static final int PRICE = 15;
	
	private Drawable image;
	
	// position, damage, delay, range, penetration, price
	public SniperTower(Tile position) {
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
