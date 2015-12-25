package isogame.comptroller;

import isogame.battle.Turn;

public abstract class Command {
	public abstract void docmd(Turn turn);
}

