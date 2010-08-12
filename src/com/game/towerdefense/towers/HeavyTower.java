package com.game.towerdefense.towers;



import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;

public class HeavyTower extends AbstractTower {
	
	private Drawable image;
	
	// position, damage, delay, range, penetration, price
	public HeavyTower(Tile position) {
		super(position, 70, 100, 100, 50, 20);
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
