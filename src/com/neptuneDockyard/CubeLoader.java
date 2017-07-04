package com.neptuneDockyard;

import java.util.ArrayList;

import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class CubeLoader {
	
	public static Object3D cube = null;
	public static ArrayList<Object3D> cubes = null;
	private int totalCubes = 0;
	private float cubeSize = 0;
	
	public CubeLoader(float size) {
		this.cubeSize = size;
		cubes = new ArrayList<Object3D>();
		Logger.log("Created cubeLoader with size: "+size);
	}
	
	public void loadCube(SimpleVector pos, String texture) {
		float cubeSize = this.cubeSize;
		cube = Primitives.getCube(cubeSize);
		cube.translate(pos);
		cube.setTexture(texture);
		cube.setSpecularLighting(true);
		cube.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		cube.enableCollisionListeners();
		cube.setCollisionOptimization(true);
		cube.calcNormals();
		cube.calcBoundingBox();
		cube.calcTextureWrap();
		cubes.add(cube);
		this.setTotalCubes(this.getTotalCubes() + 1);
	}
	
	public void removeCube(String name) {
		cubes.remove(name);
	}
	
	public void removeCube(int index) {
		cubes.remove(index);
	}
	
	public void setTexture(int index, String texture) {
		cubes.get(index).setTexture(texture);
	}
	
	public int getTotalCubes() {
		return totalCubes;
	}

	public void setTotalCubes(int totalCubes) {
		this.totalCubes = totalCubes;
	}
	
	public void setPosition(int index, SimpleVector pos) {
		cubes.get(index).translate(pos);
	}

	public Object3D getCube(int index) {
		return cubes.get(index);
	}
}
