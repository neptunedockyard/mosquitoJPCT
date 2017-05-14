package com.neptuneDockyard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;

public class ScreenLog {

	private Camera camera;
	private FrameBuffer buffer;

	public ScreenLog(Camera camera) {
		this.camera = camera;
	}
	
	public ScreenLog(FrameBuffer buffer) {
		this.buffer = buffer;
		buffer.getGraphics().setFont(new Font("Dialog", Font.PLAIN, 64));
		buffer.getGraphics().setColor(Color.white);
	}

	public void update() {
		buffer.getGraphics().drawString("Test", 100, 100);
	}
}
