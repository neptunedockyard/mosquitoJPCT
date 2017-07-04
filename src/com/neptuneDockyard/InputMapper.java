package com.neptuneDockyard;

import java.awt.event.KeyEvent;

import org.lwjgl.openal.AL;

import com.threed.jpct.Camera;
import com.threed.jpct.CollisionEvent;
import com.threed.jpct.IRenderer;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.util.KeyMapper;
import com.threed.jpct.util.KeyState;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;

public class InputMapper {
	// controllers for inputs
	
	private MouseMapper mouseMap;
	private KeyMapper keyMap;
	private KeyState state;
	private Logger logger;
	
	// camera
	
	private Camera camera;
	private Player player;
	
	// key flags

	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean forward = false;
	private boolean back = false;
	private boolean zoomLock = false;
	private boolean sprint = false;
	private float camSpeed = (float) 0.1;
	private boolean rotateCCW = false;
	private boolean rotateCW = false;
	private boolean upReset = false;
	
	// camera default orientation
	
	private SimpleVector upStart;
	private SimpleVector dirStart;
	
	public InputMapper(Player player, KeyMapper keyMap, MouseMapper mouseMap, Logger logger, GameConfig game_config) {
		// TODO Auto-generated constructor stub
		this.setPlayer(player);
		this.mouseMap = mouseMap;
		this.keyMap = keyMap;
		this.camera = player.getCamera();
		this.logger = logger;
		this.setDirStart(camera.getDirection());
		this.setUpStart(camera.getUpVector());
	}

	public void update() {
		// TODO Auto-generated method stub
		while ((state = keyMap.poll()) != KeyState.NONE) {

			if (state.getKeyCode() == KeyEvent.VK_A
					&& state.getState() == KeyState.PRESSED) {
				left = state.getState();
				logger.log("key pressed: a " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_A
					&& state.getState() == KeyState.RELEASED) {
				left = false;
				logger.log("key released: a " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_D
					&& state.getState() == KeyState.PRESSED) {
				right = state.getState();
				logger.log("key pressed: d " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_D
					&& state.getState() == KeyState.RELEASED) {
				right = false;
				logger.log("key released: d " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_SPACE
					&& state.getState() == KeyState.PRESSED) {
				up = state.getState();
				logger.log("key pressed: space "+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_SPACE
					&& state.getState() == KeyState.RELEASED) {
				up = false;
				logger.log("key released: space "+ camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_Z
					&& state.getState() == KeyState.PRESSED) {
				down = state.getState();
				logger.log("key pressed: z " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_Z
					&& state.getState() == KeyState.RELEASED) {
				down = false;
				logger.log("key released: z " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_W
					&& state.getState() == KeyState.PRESSED) {
				forward = state.getState();
				logger.log("key pressed: w " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_W
					&& state.getState() == KeyState.RELEASED) {
				forward = false;
				logger.log("key released: w " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_S
					&& state.getState() == KeyState.PRESSED) {
				back = state.getState();
				logger.log("key pressed: s " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_S
					&& state.getState() == KeyState.RELEASED) {
				back = false;
				logger.log("key released: s " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_SHIFT
					&& state.getState() == KeyState.PRESSED) {
				sprint = state.getState();
				logger.log("key pressed: shift "+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_SHIFT
					&& state.getState() == KeyState.RELEASED) {
				sprint = false;
				logger.log("key released: shift "+ camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_Q
					&& state.getState() == KeyState.PRESSED) {
				rotateCCW = state.getState();
				logger.log("key pressed: Q "+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_Q
					&& state.getState() == KeyState.RELEASED) {
				rotateCCW = false;
				logger.log("key released: Q "+ camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_E
					&& state.getState() == KeyState.PRESSED) {
				rotateCW = state.getState();
				logger.log("key pressed: E "+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_E
					&& state.getState() == KeyState.RELEASED) {
				rotateCW = false;
				logger.log("key released: E "+ camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_R
					&& state.getState() == KeyState.PRESSED) {
				upReset = state.getState();
				logger.log("key pressed: R "+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_R
					&& state.getState() == KeyState.RELEASED) {
				upReset = false;
				logger.log("key released: R "+ camera.getPosition().toString());
			}

			// lock in camera
			if (state.getState() == KeyState.PRESSED
					&& state.getKeyCode() == KeyEvent.VK_L)
				zoomLock ^= true;

			// exit game
			if (state.getState() == KeyState.PRESSED
					&& state.getKeyCode() == KeyEvent.VK_ESCAPE)
				gameShutdown();
		}
	}

	public void updatePosition() {
		SimpleVector line = new SimpleVector(mouseMap.getMouseDX(), 0, mouseMap.getMouseDY());
		Matrix m = line.normalize().getRotationMatrix();
		
		if (sprint)
			camSpeed = 10;
		else
			camSpeed = (float) 0.1;

		if (left)
			camera.moveCamera(Camera.CAMERA_MOVELEFT, camSpeed);
		if (right)
			camera.moveCamera(Camera.CAMERA_MOVERIGHT, camSpeed);
		if (up)
			camera.moveCamera(Camera.CAMERA_MOVEUP, camSpeed);
		if (down)
			camera.moveCamera(Camera.CAMERA_MOVEDOWN, camSpeed);
		if (forward)
			camera.moveCamera(Camera.CAMERA_MOVEIN, camSpeed);
		if (back)
			camera.moveCamera(Camera.CAMERA_MOVEOUT, camSpeed);
		if (rotateCCW) {
			m.rotateAxis(m.getXAxis(), (float) 0.01);
			camera.rotateAxis(m.invert3x3().getXAxis(), (float) -0.01);
		}
		if (rotateCW) {
			m.rotateAxis(m.getXAxis(), (float) -0.01);
			camera.rotateAxis(m.invert3x3().getXAxis(), (float) 0.01);
		}
		if (upReset) {
//			camera.setOrientation(this.getDirStart(), this.getUpStart());
//			camera.setOrientation(camera.getSideVector(), camera.getUpVector());
			camera.lookAt(line);
			mouseMap.setMouseDX(0);
			mouseMap.setMouseDY(0);
		}
	}
	
	private void gameShutdown() {
		keyMap.destroy();
		AL.destroy();
		System.exit(0);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public KeyMapper getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(KeyMapper keyMap) {
		this.keyMap = keyMap;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public SimpleVector getUpStart() {
		return upStart;
	}

	public void setUpStart(SimpleVector upStart) {
		this.upStart = upStart;
	}

	public SimpleVector getDirStart() {
		return dirStart;
	}

	public void setDirStart(SimpleVector dirStart) {
		this.dirStart = dirStart;
	}
}
