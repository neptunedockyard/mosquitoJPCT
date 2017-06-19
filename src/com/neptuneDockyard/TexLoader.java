package com.neptuneDockyard;

import com.threed.jpct.Logger;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class TexLoader {

	public void getList() {
		// TODO Auto-generated method stub
		
	}

	public void load() {
		// TODO Auto-generated method stub
		try {
			Logger.log("loading textures");
			TextureManager.getInstance().addTexture("starTex", new Texture("assets/textures/stars.jpg"));
			TextureManager.getInstance().addTexture("fighterTex", new Texture("assets/textures/fighter-tex.jpg"));
			TextureManager.getInstance().addTexture("sandTex", new Texture("assets/textures/sf-01.jpg"));
			TextureManager.getInstance().addTexture("truckTex", new Texture("assets/textures/veh_kama.png"));
			Logger.log("finished loading textures");
		} catch (Exception ex) {
			Logger.log("error: textures not loaded");
			Logger.log(ex.getMessage());
		}
	}

}
