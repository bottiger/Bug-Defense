package com.game.towerdefense;

import java.util.LinkedList;
import java.util.List;
import android.graphics.drawable.Drawable;
import com.game.towerdefense.creeps.*;

public class Wave {
	
	private List<Creep>creepWave = new LinkedList<Creep>();
	private int creepsNumber = 0;
	private long lastRelease;
	
	/**
	 * The time between two creeps in milliseconds
	 */
	private final int RELEASE_THRESSHOLD = 2000;
	
	void addCreeps(Creep c) {
		creepWave.add(c);
		creepsNumber++;
	}
	
	void addCreeps(Creep c, Drawable d) {
		c.setImage(d);
		creepWave.add(c);
		creepsNumber++;
	}
	
	Creep release() {
		if (thressholdReaced())
			return doRelase();
		return null;
	}
	
	Creep release(boolean force) {
		if (force)
			return doRelase();
		return null;
	}
	
	private boolean thressholdReaced() {
		return System.currentTimeMillis() > (lastRelease + RELEASE_THRESSHOLD);
	}
	
	private Creep doRelase() {
		try {
			creepsNumber--;
			lastRelease = System.currentTimeMillis();
			
			Creep c = creepWave.get(0);
			creepWave.remove(0);
			return c;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	boolean hasCreeps() {
		return !creepWave.isEmpty();
	}
	
	boolean shouldRelease() {
		return hasCreeps() && thressholdReaced();
	}

}
