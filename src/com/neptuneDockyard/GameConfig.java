package com.neptuneDockyard;

import com.threed.jpct.Logger;

public class GameConfig {

	private boolean ufo;
	
	public GameConfig() {
		Logger.log("Initializing game configuration");
	}
	
	public void set_fly(boolean value) {
		this.ufo = value;
	}
	
	public boolean get_fly() {
		return this.ufo;
	}
}
