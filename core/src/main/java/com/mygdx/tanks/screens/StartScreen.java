package com.mygdx.tanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.Tanks;
import com.mygdx.tanks.components.BackgroundView;

public class StartScreen extends ScreenAdapter {
    Tanks myGdxGame;
    private StretchViewport uiViewport;
    BackgroundView startBackground;

    private long startTime;
    private static final long SPLASH_DURATION = 2000;

    public StartScreen(Tanks myGdxGame) {
        this.myGdxGame = myGdxGame;

        uiViewport = new StretchViewport(GameSettings.UI_VIEWPORT_WIDTH, GameSettings.UI_VIEWPORT_HEIGHT);
        uiViewport.apply(true);

        startBackground = new BackgroundView(
            GameResources.START_BACKGROUND_IMG_PATH,
            GameSettings.UI_VIEWPORT_WIDTH,
            GameSettings.UI_VIEWPORT_HEIGHT
        );
        startTime = TimeUtils.millis();

        myGdxGame.audioManager.startScreenSound.play();
    }

    @Override
    public void render(float delta) {
        long elapsed = TimeUtils.millis() - startTime;

        if (elapsed > SPLASH_DURATION) myGdxGame.setScreen(myGdxGame.homeScreen);

        draw();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        int bw = Gdx.graphics.getBackBufferWidth();
        int bh = Gdx.graphics.getBackBufferHeight();
        if (bw > 0 && bh > 0) {
            resize(bw, bh);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.CLEAR);
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiViewport.apply();
        myGdxGame.batch.setProjectionMatrix(uiViewport.getCamera().combined);

        myGdxGame.batch.begin();

        startBackground.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }
}
