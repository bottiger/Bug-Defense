package com.game.towerdefense;

import java.util.ArrayList;

public class Route {

	private int currentPos = 0;

	private ArrayList<Coordinate> path = new ArrayList<Coordinate>();
	//private ArrayList<Coordinate> checkPoints;

	public Route(Coordinate startPos, Coordinate endPos,
			ArrayList<Coordinate> checkPoints) {
		calculateRoute(startPos, endPos, checkPoints);
	}
	
	public Route(Coordinate startPos, Coordinate endPos) {
		calculateRoute(startPos, endPos, new ArrayList<Coordinate>());
	}

	public void move() {
		currentPos++;
	}

	public Coordinate getCurrentPos() {
		return path.get(currentPos);
	}

	public Coordinate getNextPos() {
		if (path.get(currentPos).equals(getLastPos())) {
			return path.get(currentPos);
		} else {
			return path.get(currentPos + 1);
		}
	}

	public Coordinate getLastPos() {
		return path.get(path.size() - 1);
	}

	public boolean isEndPos() {
		return path.size() - 1 == currentPos;
	}
	
	public ArrayList<Coordinate> getPath() {
		return path;
	}

	private void calculateRoute(Coordinate startPos, Coordinate endPos,
			ArrayList<Coordinate> checkPoints) {
		
		Coordinate start = null;
		Coordinate end = null;
		
		checkPoints.add(endPos);

		for (Coordinate cp : checkPoints) {

			start = (cp == checkPoints.get(0)) ? startPos : end;
			end   = cp;
			
			for (int i = start.x; i != end.x; i = (int) (i + Math.signum(end.x - start.x)))
				this.path.add(new Coordinate(i, start.y));

			for (int j = start.y; j != end.y; j = (int) (j + Math.signum(end.y - start.y)))
				this.path.add(new Coordinate(end.x, j));
		}
	}

}
