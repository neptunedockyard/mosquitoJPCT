package com.neptuneDockyard;

import org.lwjgl.input.Mouse;

import com.threed.jpct.Camera;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.Light;

public class MouseMapper {
	
	private Camera camera;
	private Light floodLight;
	private Player player;
	private int dx;
	private int dy;
	private float xAngle = 0;
	
	public MouseMapper(Camera camera) {
		this.camera = camera;
	}

	public void cameraUpdate() {
		dx = Mouse.getDX();
		dy = Mouse.getDY();
		
		if (dx != 0) {
			camera.rotateAxis(camera.getYAxis(), dx / 500f);
		}

		if ((dy > 0 && xAngle < Math.PI / 4.2) || (dy < 0 && xAngle > -Math.PI / 4.2)) {
		    float t=dy/500f;
			camera.rotateX(t);
			xAngle += t;
		}
//		camera.moveCamera(camera.move, 0.1f);
//		find some way to find direction of fall from camera to body and move camera in that direction
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setMouseDX(int x) {
		this.dx = x;
	}
	
	public int getMouseDX() {
		return dx;
	}
	
	public void setMouseDY(int y) {
		this.dy = y;
	}
	
	public int getMouseDY() {
		return dy;
	}
	
	public SimpleVector getCameraPosition() {
		Logger.log("X: "+camera.getPosition().x+" Y: "+camera.getPosition().y+" Z: "+camera.getPosition().z);
		return this.camera.getPosition();
	}

}
