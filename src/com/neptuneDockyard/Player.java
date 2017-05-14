package com.neptuneDockyard;

import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;

public class Player {

	private Camera camera;
	private SimpleVector cameraPosition;
	private int fov;

	public Player(Camera camera) {
		this.camera = camera;
	}

	public SimpleVector getCameraPosition() {
		return cameraPosition;
	}

	public void setCameraPosition(SimpleVector cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public int getFov() {
		return fov;
	}

	public void setFov(int fov) {
		this.fov = fov;
		this.camera.setFOV(fov);
	}

}
