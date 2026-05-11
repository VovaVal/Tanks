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

    /**
     * Заполняет прямоугольник destW×destH текстурой с сохранением пропорций (как object-fit: cover).
     * Используется под игровой FitViewport, чтобы по бокам не было пустого/чёрного фона.
     */
    public void drawCover(SpriteBatch batch, float destW, float destH) {
        float tw = texture.getWidth();
        float th = texture.getHeight();
        float scale = Math.max(destW / tw, destH / th);
        float drawW = tw * scale;
        float drawH = th * scale;
        float x = (destW - drawW) * 0.5f;
        float y = (destH - drawH) * 0.5f;
        batch.draw(texture, x, y, drawW, drawH);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}

