package com.mygdx.tanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.Tanks;
import com.mygdx.tanks.components.BackgroundView;
import com.mygdx.tanks.components.ButtonView;

public class HomeScreen extends ScreenAdapter {
    Tanks myGdxGame;
    ButtonView level1;
    ButtonView level2;
    ButtonView level3;
    ButtonView level4;
    ButtonView level5;
    ButtonView level6;
    ButtonView level7;
    ButtonView level;
    ButtonView level9;
    ButtonView level10;
    BackgroundView homeBackground;

    public HomeScreen(Tanks myGdxGame) {
        this.myGdxGame = myGdxGame;

        homeBackground = new BackgroundView(GameResources.HOME_BACKGROUND_IMG_PATH, 2500, 1100);
    }

    @Override
    public void render(float delta) {
        draw();
    }

    private void draw() {
        ScreenUtils.clear(Color.CLEAR);
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        myGdxGame.batch.begin();

        homeBackground.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }
}
