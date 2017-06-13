package com.neptuneDockyard;

import java.util.Random;

import com.threed.jpct.GenericVertexController;
import com.threed.jpct.Logger;
import com.threed.jpct.SimpleVector;

public class SurfaceMod extends GenericVertexController {

	private static final long serialVersionUID = 1L;
	private static int noiseMult = 3;
	private static final int max = 1;
	private static final int min = -1;

	public void setNoise(int planetSize) {
		// TODO Auto-generated constructor stub
		this.noiseMult = planetSize % this.noiseMult;
	}

	public void apply() {
		// TODO Auto-generated method stub
		SimpleVector[] s = getSourceMesh();
		SimpleVector[] d = getDestinationMesh();
		for (int i = 0; i < s.length; i++) {
			d[i].z = s[i].z	+ (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)* randomOp();
			d[i].y = s[i].y	+ (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)* randomOp();
			d[i].x = s[i].x	+ (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)* randomOp();
		}
		Logger.log("Planet surface modder applied");
	}
	
	private float randomOp() {
		Random rn = new Random();
		return randomBetween(noiseMult)*(rn.nextInt((max+1) - min) + min);
	}
	
	private float randomBetween(int noise) {
		Random rn = new Random();
		return rn.nextFloat()*((noise+1) + noise) - noise;
	}
}
