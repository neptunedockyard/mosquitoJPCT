package com.neptuneDockyard;

import java.util.Random;

import com.threed.jpct.IVertexController;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class PlanetLoader {

	public Object3D[] universe = null;
	private int totalPlanets;
	
	public PlanetLoader(int totalPlanets) {
		// TODO Auto-generated constructor stub
		this.totalPlanets = totalPlanets;
		universe = new Object3D[totalPlanets];
		Logger.log("Total planets created: "+universe.length);
	}

	public Object3D[] generate() {
		// TODO Auto-generated method stub
		for(int i = 0; i < this.totalPlanets; i++) {
			int planetSize = randomBetween(20,100);
			universe[i] = Primitives.getSphere(32, planetSize);
			universe[i].rotateX((float) (Math.PI / 2f));
			universe[i].setSpecularLighting(true);
			universe[i].setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			universe[i].enableCollisionListeners();
			universe[i].setCollisionOptimization(true);
			universe[i].setTexture("sandTex");
			universe[i].compileAndStrip();
			
			this.modifySurface(universe[i]);
			
			universe[i].setName("Planet_"+i);
			Logger.log("Planet "+i+" generated with size "+planetSize);
		}
		return universe;
	}

	public void place(int distance) {
		// TODO Auto-generated method stub
		for(int i = 0; i < this.totalPlanets; i++) {
			SimpleVector position = new SimpleVector(randomBetween(-distance, distance),randomBetween(-distance, distance),randomBetween(-distance, distance));
			universe[i].translate(position);
			Logger.log("Planet "+i+" placed at "+position);
		}
	}
	
	private int randomBetween(int min, int max) {
		Random rn = new Random();
		return rn.nextInt((max+1) - min) + min;
	}
	
	public void modifySurface(Object3D obj) {
		Logger.log("Loading surface modifier");
		Mesh planeMesh = obj.getMesh();
		planeMesh.setVertexController(new SurfaceMod(), false);
		planeMesh.applyVertexController();
		planeMesh.removeVertexController();
	}

	public void modifyShape(Object3D obj) {
		Logger.log("Loading shape modifier");
		Mesh objMesh = obj.getMesh();
		objMesh.setVertexController(null, true);
		objMesh.applyVertexController();
		objMesh.removeVertexController();
	}

	public Object3D[] getUniverse() {
		// TODO Auto-generated method stub
		return universe;
	}
	
	public void applyTexture(int planet, String texture) {
		universe[planet].setTexture(texture);
	}

}
