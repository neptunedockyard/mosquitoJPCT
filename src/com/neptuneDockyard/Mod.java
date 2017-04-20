package com.neptuneDockyard;

import com.threed.jpct.GenericVertexController;
import com.threed.jpct.SimpleVector;

public class Mod extends GenericVertexController {

	private static final long serialVersionUID = 1L;
	private static final int noiseMult = 4;

	public void apply() {
		// TODO Auto-generated method stub
		SimpleVector[] s = getSourceMesh();
		SimpleVector[] d = getDestinationMesh();
		for (int i = 0; i < s.length; i++) {
			// d[i].z = s[i].z - (10f * ((float) Math.sin(s[i].x / 50f) + 2f *
			// (float) Math.cos(s[i].y / 50f)));
			// d[i].z = FastNoise.noise(d[i].x, d[i].y, 7) % 32;
			d[i].z = s[i].z
					- (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)
					* noiseMult;
			d[i].x = s[i].x;
			d[i].y = s[i].y;
		}
	}
}
