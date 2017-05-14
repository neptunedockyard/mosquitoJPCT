/**
 * 
 */
package com.neptuneDockyard;

import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Lights;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.KeyMapper;
import com.threed.jpct.util.KeyState;
import com.threed.jpct.util.Light;
import com.threed.jpct.util.SkyBox;

public class Engine {

	public Logger logger = new Logger();

	// JPCT controllers

	private KeyMapper keyMap = null;
	private MouseMapper mouseMap = null;
	private KeyState state = null;
	private InputMapper inMap = null;

	// private controllers

	private Player player = null;
	private ScreenLog screenLog = null;

	// JPCT variables

	private FrameBuffer buffer = null;
	private World theWorld = null;
	private TextureManager texMan = null;
	private Camera camera = null;
	private SkyBox skyBox = null;
	
	// game configuration
	
	private GameConfig gameConfig = null;
	
	// collision listeners
	
	private Object3D firstPlanet;
	private Light testLight = null;

	// textures

	// player location

	private Matrix playerDirection = new Matrix();

	// audio and music

	private Audio oggStream = null;

	// methods
	
	public void logging_init() {
		Logger.setLogLevel(Logger.LL_VERBOSE);
		Logger.log("Log level set: " + Integer.toString(Logger.getLogLevel()));
	}
	
	public Engine(String[] args) {
		// TODO evaluate args here

		logging_init();
		Logger.log("Starting Engine");
		
		// init world instance and get TextureManager

		theWorld = new World();
		texMan = TextureManager.getInstance();
		
		// set up game configuration
		
		gameConfig = new GameConfig();
		gameConfig.setFly(true);
		gameConfig.setPlayMusic(false);
		gameConfig.setFullscreen(true);
		gameConfig.setWidth(Display.getDisplayMode().getWidth());
		gameConfig.setHeight(Display.getDisplayMode().getHeight());

		// set up mouse

		Mouse.setGrabbed(true);

		// set up lighting

		theWorld.getLights().setOverbrightLighting(Lights.OVERBRIGHT_LIGHTING_ENABLED);
		theWorld.getLights().setRGBScale(Lights.RGB_SCALE_2X);
		testLight = new Light(theWorld);
		testLight.setPosition(new SimpleVector(150, 150, 0));
		testLight.setIntensity(0, 255, 255);
		testLight.setAttenuation(-1);
		

		// place light sources

		theWorld.addLight(new SimpleVector(100, 100, 100), 5, 10, 15);
		theWorld.addLight(testLight.getPosition(), Color.white);

		// add fog

		theWorld.setFogging(World.FOGGING_DISABLED);
		theWorld.setFoggingMode(World.FOGGING_PER_PIXEL);
		theWorld.setFogParameters(400, 30, 30, 30);

		// add textures

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

		// add model

		try {
			// load player model
			Logger.log("loading player model");
			
			Logger.log("loading NPC models");
			
			// TODO load surface
			firstPlanet = Primitives.getSphere(32, 200);
			firstPlanet.rotateX((float) (Math.PI / 2f));
			firstPlanet.setSpecularLighting(true);
			firstPlanet.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			firstPlanet.enableCollisionListeners();
			firstPlanet.setCollisionOptimization(true);
			firstPlanet.setTexture("sandTex");
			firstPlanet.compileAndStrip();
			
			Mesh planeMesh = firstPlanet.getMesh();
			planeMesh.setVertexController(new Mod(), false);
			planeMesh.applyVertexController();
			planeMesh.removeVertexController();
			
			firstPlanet.translate(100, 100, 100);
			firstPlanet.setName("firstPlanet");
			theWorld.addObject(firstPlanet);

			// load shadows and projector

			Loader.clearCache();
			Logger.log("finished loading models");
		} catch (Exception ex) {
			// TODO fix loader error, check if model loaded instead of the try catch method
			Logger.log("error: models not loaded");
			Logger.log(ex.getMessage());
		}

		// add sounds

		try {
			Logger.log("loading sounds");
			oggStream = AudioLoader.getStreamingAudio("OGG",
					ResourceLoader.getResource("assets/audio/infini1.ogg"));
		} catch (Exception ex) {
			Logger.log("error: sounds not loaded");
			Logger.log(ex.getMessage());
		}

		// add skybox
		skyBox = new SkyBox(1000f);
		skyBox.compile();

		// add camera
		Logger.log("adding camera, setting position");
		camera = theWorld.getCamera();
		camera.setFOV(60);
		camera.setPosition(50, -50, -5);

		// add buffer
		Logger.log("adding framebuffer");
		buffer = new FrameBuffer(gameConfig.getWidth(), gameConfig.getHeight(), FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
		
		// now go to game loop
		theWorld.buildAllObjects();
	}

	public void init() {
		Logger.log("Engine init");

		gameConfig = new GameConfig();
		player = new Player(camera);
		keyMap = new KeyMapper();
		mouseMap = new MouseMapper(camera);
		inMap = new InputMapper(player, keyMap, mouseMap, logger, gameConfig);
		screenLog = new ScreenLog(buffer);
	}

	public void run() {
		Logger.log("Engine running");

		if(gameConfig.isPlayMusic()) oggStream.playAsMusic(1.0f, 1.0f, true);
		gameLoop();
	}

	public void update() {
		SoundStore.get().poll(0);

		mouseMap.cameraUpdate();
		inMap.update();
		inMap.updatePosition();
		screenLog.update();
	}

	public void gameLoop() {
		Logger.log("Game loop");

		while (!org.lwjgl.opengl.Display.isCloseRequested()) {
			update();
			buffer.clear(java.awt.Color.BLACK);
			buffer.setPaintListenerState(false);

			// render skybox
			skyBox.render(theWorld, buffer);

			theWorld.renderScene(buffer);
			theWorld.drawWireframe(buffer, java.awt.Color.BLACK);
			theWorld.draw(buffer);
			buffer.setPaintListenerState(true);
			buffer.update();
			buffer.displayGLOnly();
		}

		gameShutdown();
	}

	public void gameShutdown() {
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		oggStream.stop();
		keyMap.destroy();
		AL.destroy();
		System.exit(0);
	}
}
