package com.game.towerdefense.towers;



import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;

public class GenericTower extends AbstractTower {
	
	private Drawable image;
	
	public GenericTower(Tile position) {
		super(position, 7, 20, 100, 10);
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
