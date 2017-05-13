/**
 * 
 */
package com.neptuneDockyard;

import java.awt.event.KeyEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import com.threed.jpct.Camera;
import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Lights;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.Projector;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.KeyMapper;
import com.threed.jpct.util.KeyState;
import com.threed.jpct.util.Light;
import com.threed.jpct.util.ShadowHelper;
import com.threed.jpct.util.SkyBox;

public class Engine {

	public Logger logger = new Logger();

	// JPCT controllers

	private KeyMapper keyMap = null;
	private MouseMapper mouseMap = null;
	private KeyState state = null;

	// private controllers

	private Chunk chunkCon = null;

	private boolean fullscreen = false;
	private boolean openGL = false;
	private boolean wireframe = false;

	// JPCT variables

	private Object3D playerShip = null;
	private Object3D truck = null;
	private Object3D btr = null;
	private Object3D mi24 = null;
	private Object3D mi8 = null;
	private Object3D car = null;

	private FrameBuffer buffer = null;
	private World theWorld = null;
	private TextureManager texMan = null;
	private Camera camera = null;
	private SkyBox skyBox = null;
	private Object3D skyDome = null;
	
	// collision listeners
	
	private CollisionListener planetListener = null;

	// TODO remove test object
	private Object3D testPlane = null;
	private Projector projector = null;
	private ShadowHelper sh = null;
	private Light testLight = null;

	// textures

	private Texture[] textures[] = null;

	// player location

	private Matrix playerDirection = new Matrix();
	private SimpleVector tempVector = new SimpleVector();

	// framebuffer size

	private int width = 1366;
	private int height = 768;

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
	private boolean ufo = false;				//false to enable gravity

	// audio and music

	private Audio oggStream = null;

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

		// set up config

		Config.glVerbose = true;
		Config.glAvoidTextureCopies = true;
		Config.maxPolysVisible = 1000;
		Config.glColorDepth = 32;
		Config.glFullscreen = true;
		Config.farPlane = 4000;
		Config.glShadowZBias = 0.8f;
		Config.lightMul = 1;
		Config.collideOffset = 500;
		Config.glTrilinear = true;
		Config.glWindowName = "Mosquito JPCT";

		// set up mouse

		Mouse.setGrabbed(true);

		// set up lighting

		Config.fadeoutLight = true;
		Config.linearDiv = 100;
		Config.lightDiscardDistance = 35;
		theWorld.getLights().setOverbrightLighting(
				Lights.OVERBRIGHT_LIGHTING_DISABLED);
		theWorld.getLights().setRGBScale(Lights.RGB_SCALE_2X);
//		theWorld.setAmbientLight(255, 204, 185);
		testLight = new Light(theWorld);
		testLight.setPosition(new SimpleVector(100, 100, 100));
		testLight.setIntensity(140, 120, 120);
		testLight.setAttenuation(-1);
		

		// place light sources

		theWorld.addLight(new SimpleVector(100, 100, 100), 5, 10, 15);

		// add fog

		theWorld.setFogging(World.FOGGING_ENABLED);
		theWorld.setFoggingMode(World.FOGGING_PER_PIXEL);
		theWorld.setFogParameters(400, 30, 30, 30);

		// add textures

		try {
			Logger.log("loading textures");
			Texture starTex = null;
			TextureManager.getInstance().addTexture("starTex", new Texture("assets/textures/stars.jpg"));
			Texture fighterTex = null;
			TextureManager.getInstance().addTexture("fighterTex", new Texture("assets/textures/fighter-tex.jpg"));
			Texture sandTex = null;
			TextureManager.getInstance().addTexture("sandTex", new Texture("assets/textures/sf-01.jpg"));
			Texture truckTex = null;
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
			playerShip = Loader.load3DS("assets/models/player/fighter.3ds",
					0.1f)[0];
			playerShip.rotateX((float) -Math.PI / 4f);
			playerShip.rotateMesh();

			playerShip.setTexture("fighterTex");
			// playerShip.setEnvmapped(Object3D.ENVMAP_ENABLED); //this causes texture to get all messed up
			playerShip.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			playerShip.enableCollisionListeners();
			playerShip.calcNormals();
			playerShip.build();
			playerShip.compile();
			playerShip.setName("playerShip");
			theWorld.addObject(playerShip);
			
			Logger.log("loading NPC models");
			truck = Loader.load3DS("assets/models/vehicles/truck.3ds", 10f)[0];
			truck.setTexture("truckTex");
//			truck.setEnvmapped(Object3D.ENVMAP_ENABLED);
			truck.rotateX((float) (-Math.PI / 1f));
			truck.translate(0, 50f, 0);
			truck.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			truck.enableCollisionListeners();
			truck.calcNormals();
			truck.calcBoundingBox();
			truck.build();
			truck.compile();
			truck.setName("truck");
			theWorld.addObject(truck);
			
			// TODO add c130
			btr = Loader.loadOBJ("assets/models/tanks/BTR70.obj", null, 1f)[0];
//			btr.setTexture("ac130Tex");
			btr.translate(200f, 0, 0);
			btr.rotateX((float) (-Math.PI / 1f));
			btr.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			btr.enableCollisionListeners();
			btr.calcNormals();
			btr.calcBoundingBox();
			btr.build();
			btr.compile();
			btr.setName("ac130");
//			theWorld.addObject(btr);
			
			// TODO add trucks

			// TODO load surface
//			testPlane = Primitives.getPlane(32, 32);
			testPlane = Primitives.getSphere(64, 70);
//			testPlane = Primitives.getCube(70);
			testPlane.rotateX((float) (Math.PI / 2f));
			testPlane.setSpecularLighting(true);
			testPlane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			testPlane.enableCollisionListeners();
			testPlane.setCollisionOptimization(true);
			testPlane.setTexture("sandTex");
			testPlane.compileAndStrip();
//			Mesh planeMesh = testPlane.getMesh();
//			planeMesh.setVertexController(new Mod(), false);
//			planeMesh.applyVertexController();
//			planeMesh.removeVertexController();
			testPlane.translate(100, 100, 100);
//			testPlane.translate(0, 100, 0);
			testPlane.setName("testPlane");
			theWorld.addObject(testPlane);
//			testPlane.addCollisionListener(planetListener);

			// load shadows and projector
			//TODO throws errors
//			projector = new Projector();
//			projector.setFOV(1.5f);
//			projector.setYFOV(1.5f);
//			sh = new ShadowHelper(theWorld, buffer, projector, 2048);
//			sh.setCullingMode(false);
////			 sh.setAmbientLight(new Color(30, 30, 30));
//			sh.setLightMode(true);
//			sh.setBorder(1);
//			sh.addCaster(playerShip);
//			sh.addReceiver(testPlane);

			Loader.clearCache();
			Logger.log("finished loading models");
		} catch (Exception ex) {
			// TODO fix loader error, check if model loaded instead of the try
			// catch method
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
		// TODO figure out skybox issue
		skyBox = new SkyBox(/*"starTex", "starTex", "starTex", "starTex", "starTex", "starTex",*/ 1000f);
		skyBox.compile();

		// add camera

		Logger.log("adding camera, setting position");
		camera = theWorld.getCamera();
		camera.setFOV(60);
		camera.setPosition(50, -50, -5);
		camera.lookAt(playerShip.getTransformedCenter());

		// add buffer

		Logger.log("adding framebuffer");
		buffer = new FrameBuffer(width, height, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
		
		// now go to game loop
		theWorld.buildAllObjects();
	}

	public void init() {
		Logger.log("Engine init");

		keyMap = new KeyMapper();
		mouseMap = new MouseMapper(camera, playerShip);
	}

	public void run() {
		Logger.log("Engine running");

		oggStream.playAsMusic(1.0f, 1.0f, true);
		gameLoop();
	}

	public void update() {
		SoundStore.get().poll(0);

		mouseMap.cameraUpdate();
		updatePosition();
		getCollisions();

		while ((state = keyMap.poll()) != KeyState.NONE) {

			if (state.getKeyCode() == KeyEvent.VK_A
					&& state.getState() == KeyState.PRESSED) {
				left = state.getState();
				Logger.log("key pressed: a " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_A
					&& state.getState() == KeyState.RELEASED) {
				left = false;
				Logger.log("key released: a " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_D
					&& state.getState() == KeyState.PRESSED) {
				right = state.getState();
				Logger.log("key pressed: d " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_D
					&& state.getState() == KeyState.RELEASED) {
				right = false;
				Logger.log("key released: d " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_SPACE
					&& state.getState() == KeyState.PRESSED) {
				up = state.getState();
				Logger.log("key pressed: space "
						+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_SPACE
					&& state.getState() == KeyState.RELEASED) {
				up = false;
				Logger.log("key released: space "
						+ camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_Z
					&& state.getState() == KeyState.PRESSED) {
				down = state.getState();
				Logger.log("key pressed: z " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_Z
					&& state.getState() == KeyState.RELEASED) {
				down = false;
				Logger.log("key released: z " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_W
					&& state.getState() == KeyState.PRESSED) {
				forward = state.getState();
				Logger.log("key pressed: w " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_W
					&& state.getState() == KeyState.RELEASED) {
				forward = false;
				Logger.log("key released: w " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_S
					&& state.getState() == KeyState.PRESSED) {
				back = state.getState();
				Logger.log("key pressed: s " + camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_S
					&& state.getState() == KeyState.RELEASED) {
				back = false;
				Logger.log("key released: s " + camera.getPosition().toString());
			}
			if (state.getKeyCode() == KeyEvent.VK_SHIFT
					&& state.getState() == KeyState.PRESSED) {
				sprint = state.getState();
				Logger.log("key pressed: shift "
						+ camera.getPosition().toString());
			} else if (state.getKeyCode() == KeyEvent.VK_SHIFT
					&& state.getState() == KeyState.RELEASED) {
				sprint = false;
				Logger.log("key released: shift "
						+ camera.getPosition().toString());
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
			// try {
			// Thread.sleep(10);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
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

	public void updatePosition() {
		if (sprint)
			camSpeed = 1;
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
		
		//check camera collisions, this makes the camera fall
		if(!ufo)
//			theWorld.checkCameraCollisionEllipsoid(testPlane.getTransformedCenter().normalize(), new SimpleVector(1,1,5), 2, 1);
			if (theWorld.checkCameraCollisionEllipsoid(testPlane.getTransformedCenter().normalize(), new SimpleVector(1,1,5), 1, 1)) {
				CollisionEvent ce = null;
//				Object3D ob = ce.getObject();
//				planetListener.collision(ce);
//				camera.align(ce.getSource());
			}
//			theWorld.checkCameraCollisionEllipsoid(new SimpleVector(0,1,0), new SimpleVector(1,1,5), 2, 1);
		
	}

	public void getCollisions() {
		// TODO get collisions here and put it in the update loop, if collided,
		// disable directional movement
		
		//apply gravity
		SimpleVector t = new SimpleVector(0,0,0);
		SimpleVector s = new SimpleVector(0,0,0);
		t = testPlane.getTransformedCenter().normalize();
		s = testPlane.getTransformedCenter().normalize();
//		t = theWorld.getObjectByName("playerShip").checkForCollisionEllipsoid(t, testPlane.getTransformedCenter().normalize(), 1);
//		theWorld.getObjectByName("playerShip").translate(t);
//		s = theWorld.getObjectByName("truck").checkForCollisionEllipsoid(s, testPlane.getTransformedCenter().normalize(), 1);
//		theWorld.getObjectByName("truck").translate(s);
	}
}
