package com.game.towerdefense;

public class GameState {
	
	private static int lives = 0;
	private static Bank bank;
	
	public static void setBank(Bank b) {
		bank = b;
	}
	
	public static void setLives(int l) {
		lives = l;
	}
	
	public static void decreaseLives() {
		lives -= 1;
	}
	
	public static Bank getBank() {
		return bank;
	}
	
	public static int getLives() {
		return lives;
	}
}
