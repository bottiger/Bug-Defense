package com.game.towerdefense.towers;



import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;

public class SniperTower extends AbstractTower {
	
	private Drawable image;
	
	// position, damage, delay, range, penetration, price
	public SniperTower(Tile position) {
		super(position, 10, 30, 200, 40, 15);
	}
	
	@Override
	public int getImageID() {
		return R.drawable.creeps;
	}

	@Override
	public Drawable getImage() {
		return image;
	}

	@Override
	public void setImage(Drawable img) {
		image = img;
		
	}

}
