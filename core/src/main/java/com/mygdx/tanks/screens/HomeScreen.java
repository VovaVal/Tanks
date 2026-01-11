package com.mygdx.tanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.Tanks;
import com.mygdx.tanks.components.BackgroundView;
import com.mygdx.tanks.components.ButtonView;
import com.mygdx.tanks.components.ImageView;

public class HomeScreen extends ScreenAdapter {
    Tanks myGdxGame;
    ButtonView level1;
    ButtonView level2;
    ButtonView level3;
    ButtonView level4;
    ButtonView level5;
    ButtonView level6;
    ButtonView level7;
    ButtonView level8;
    ButtonView level9;
    ButtonView level10;
    BackgroundView homeBackground;
    ImageView tanksText;
    private ScreenViewport uiViewport;
    GameScreen gameScreen;

    public HomeScreen(Tanks myGdxGame) {
        this.myGdxGame = myGdxGame;

        gameScreen = new GameScreen(this.myGdxGame);

        uiViewport = new ScreenViewport();
        uiViewport.apply(true);

        homeBackground = new BackgroundView(GameResources.HOME_BACKGROUND_IMG_PATH, 2500, 1100);

        level1 = new ButtonView(0, 300, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 1");
        level2 = new ButtonView(430, 300, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 2");
        level3 = new ButtonView(860, 300, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 3");
        level4 = new ButtonView(1290, 300, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 4");
        level5 = new ButtonView(1720, 300, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 5");
        level6 = new ButtonView(0, 50, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 6");
        level7 = new ButtonView(430, 50, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 7");
        level8 = new ButtonView(860, 50, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 8");
        level9 = new ButtonView(1290, 50, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 9");
        level10 = new ButtonView(1720, 50, 500, 300, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 10");

        tanksText = new ImageView(800, 600, GameResources.TANKS_TEXT_IMG_PATH, 700, 600);
    }

    @Override
    public void render(float delta) {
        handleInput();

        draw();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            uiViewport.unproject(touchPos);

            if (level1.isHit(touchPos.x, touchPos.y)) {
                myGdxGame.audioManager.btnClick.play();
                gameScreen.restart();
                myGdxGame.setScreen(gameScreen);
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.CLEAR);
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiViewport.apply();
        myGdxGame.batch.setProjectionMatrix(uiViewport.getCamera().combined);

        myGdxGame.batch.begin();

        homeBackground.draw(myGdxGame.batch);

        level1.draw(myGdxGame.batch);
        level2.draw(myGdxGame.batch);
        level3.draw(myGdxGame.batch);
        level4.draw(myGdxGame.batch);
        level5.draw(myGdxGame.batch);
        level6.draw(myGdxGame.batch);
        level7.draw(myGdxGame.batch);
        level8.draw(myGdxGame.batch);
        level9.draw(myGdxGame.batch);
        level10.draw(myGdxGame.batch);

        tanksText.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    @Override
    public void dispose() {
        if (homeBackground != null) homeBackground.dispose();

        if (level1 != null) level1.dispose();
        if (level2 != null) level2.dispose();
        if (level3 != null) level3.dispose();
        if (level4 != null) level4.dispose();
        if (level5 != null) level5.dispose();
        if (level6 != null) level6.dispose();
        if (level7 != null) level7.dispose();
        if (level8 != null) level8.dispose();
        if (level9 != null) level9.dispose();
        if (level10 != null) level10.dispose();

        if (tanksText != null) tanksText.dispose();
    }
}
