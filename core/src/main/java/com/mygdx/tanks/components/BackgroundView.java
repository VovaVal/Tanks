package com.mygdx.tanks.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.tanks.GameSettings;

public class BackgroundView extends View {

    Texture texture;

    public BackgroundView(String pathToTexture) {
        super(0, 0);
        texture = new Texture(pathToTexture);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, 0, 0, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}

