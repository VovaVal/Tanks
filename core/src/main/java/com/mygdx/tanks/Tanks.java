package com.mygdx.tanks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.tanks.managers.AudioManager;
import com.mygdx.tanks.screens.GameScreen;
import com.mygdx.tanks.screens.HomeScreen;

import static com.mygdx.tanks.GameSettings.*;

public class Tanks extends Game {

    public SpriteBatch batch;
    public OrthographicCamera camera;

    public World world;
    public Vector3 touch;
    float accumulator = 0;

    public GameScreen gameScreen;
    public HomeScreen homeScreen;

    public AudioManager audioManager;

    public BitmapFont largeWhiteFont;
    public BitmapFont commonWhiteFont;
    public BitmapFont commonBlackFont;

    @Override
    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, 0), true);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        audioManager = new AudioManager();

        largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);
        commonWhiteFont = FontBuilder.generate(24, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(24, Color.BLACK, GameResources.FONT_PATH);

        // gameScreen = new GameScreen(this);
        homeScreen = new HomeScreen(this);

        setScreen(homeScreen);
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += delta;

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    public void resetWorld() {
        if (world != null) {
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);

            for (Body body : bodies) {
                world.destroyBody(body);
            }

            world.setContactListener(null);
        }

        world = new World(new Vector2(0, 0), true);

        System.out.println("New world created. Body count: " + world.getBodyCount());
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
