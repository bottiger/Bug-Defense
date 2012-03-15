package com.game.towerdefense.towers;



import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;

public class GenericTower extends AbstractTower {
	
	private static final int DAMAGE = 7;
	private static final int DELAY = 20;
	private static final int RANGE = 100;
	private static final int PENETRATION = 10;
	private static final int PRICE = 10;
	
	private Drawable image;
	
	// position, damage, delay, range, penetration, price
	public GenericTower(Tile position) {
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
