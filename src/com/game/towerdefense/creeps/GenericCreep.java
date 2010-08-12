package com.game.towerdefense.creeps;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.game.towerdefense.Tile;
import com.game.towerdefense.R;
import com.game.towerdefense.Route;

public class GenericCreep extends AbstractCreep {
	
	private Drawable image;

	public GenericCreep(int speed, int health, int armour, Route route) {
		super(speed, health, armour, 1, route);
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
