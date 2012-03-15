package com.game.towerdefense;

public class Bank {

	private static int money = 0;
	
	public Bank(int amount) {
		money = amount;
	}
	
	static void setAmount(int amount) {
		money = amount;
	}
	
	public static int getAmount() {
		return money;
	}
	
	public static void decreaseAmount(int amount) {
		money -= amount;
	}
	
	public static void increaseMoney(int amount) {
		money += amount;
	}
}
