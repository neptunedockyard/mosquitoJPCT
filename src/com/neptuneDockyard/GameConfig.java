package com.neptuneDockyard;

import com.threed.jpct.Config;
import com.threed.jpct.Logger;

public class GameConfig {
	
	private Config config;
	private boolean fly;
	private boolean playMusic;
	private boolean fullscreen;
	private boolean openGL;
	private boolean wireframe;
	private int width = 1366;
	private int height = 768;
	
	public GameConfig() {
		Logger.log("Initializing game configuration");
		
		// set up config

		Config.glVerbose = true;
		Config.glAvoidTextureCopies = true;
		Config.maxPolysVisible = 1000;
		Config.glColorDepth = 32;
		Config.glFullscreen = fullscreen;
		Config.farPlane = 8000;
		Config.glShadowZBias = 0.8f;
		Config.lightMul = 1;
		Config.collideOffset = 500;
		Config.glTrilinear = true;
		Config.glWindowName = "Mosquito JPCT";
		Config.fadeoutLight = true;
		Config.linearDiv = 50;
		Config.lightDiscardDistance = -1;
	}

	public void setFly(boolean value) {
		this.fly = value;
	}
	
	public boolean isFly() {
		return this.fly;
	}

	public boolean isPlayMusic() {
		return playMusic;
	}

	public void setPlayMusic(boolean playMusic) {
		this.playMusic = playMusic;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
		Config.glFullscreen = fullscreen;
	}

	public boolean isOpenGL() {
		return openGL;
	}

	public void setOpenGL(boolean openGL) {
		this.openGL = openGL;
	}

	public boolean isWireframe() {
		return wireframe;
	}

	public void setWireframe(boolean wireframe) {
		this.wireframe = wireframe;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
