package com.mygdx.tanks.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.tanks.GameSettings;

public class BackgroundView extends View {

    Texture texture;
    private int width = 0;
    private int height = 0;

    public BackgroundView(String pathToTexture) {
        super(0, 0);
        texture = new Texture(pathToTexture);
    }

    public BackgroundView(String pathToTexture, int width, int height) {
        super(0, 0);
        texture = new Texture(pathToTexture);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (height == 0)
            batch.draw(texture, 0, 0, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
        else
            batch.draw(texture, 0, 0, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}

