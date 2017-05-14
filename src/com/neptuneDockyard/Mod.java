package com.neptuneDockyard;

import java.util.Random;

import com.threed.jpct.GenericVertexController;
import com.threed.jpct.SimpleVector;

public class Mod extends GenericVertexController {

	private static final long serialVersionUID = 1L;
	private static final int noiseMult = 5;
	private static final int max = 1;
	private static final int min = -1;

	public void apply() {
		// TODO Auto-generated method stub
		SimpleVector[] s = getSourceMesh();
		SimpleVector[] d = getDestinationMesh();
		for (int i = 0; i < s.length; i++) {
			d[i].z = s[i].z	+ (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)* randomOp();
			d[i].y = s[i].y	+ (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)* randomOp();
			d[i].x = s[i].x	+ (float) SimplexNoise.noise(s[i].x, s[i].y, s[i].z)* randomOp();
		}
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
