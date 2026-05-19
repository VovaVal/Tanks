package com.mygdx.tanks.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.tanks.GamePlayMode;
import com.mygdx.tanks.GameResources;

/**
 * Переключатель «один / с друзьями» — два сегмента в одной полосе.
 */
public class ModeToggleView extends View {

    private final Texture texture;
    private final BitmapFont font;
    private GamePlayMode mode = GamePlayMode.SINGLE;

    public ModeToggleView(BitmapFont font) {
        super(0, 0);
        this.font = font;
        texture = new Texture(GameResources.BUTTON_IMG_PATH);
    }

    public GamePlayMode getMode() {
        return mode;
    }

    public void setMode(GamePlayMode mode) {
        this.mode = mode;
    }

    /** -1 — нет попадания; 0 — solo; 1 — friends */
    public int hitSegment(float tx, float ty) {
        if (!isHit(tx, ty)) return -1;
        float half = width * 0.5f;
        if (tx < x + half) return 0;
        return 1;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (width <= 0 || height <= 0) return;

        float half = width * 0.5f;
        float activeA = 1f;
        float inactiveA = 0.5f;

        batch.setColor(0.12f, 0.12f, 0.16f, 0.85f);
        batch.draw(texture, x - 6, y - 6, width + 12, height + 12);
        batch.setColor(Color.WHITE);

        batch.setColor(1f, 1f, 1f, mode == GamePlayMode.SINGLE ? activeA : inactiveA);
        batch.draw(texture, x, y, half, height);
        batch.setColor(1f, 1f, 1f, mode == GamePlayMode.WITH_FRIENDS ? activeA : inactiveA);
        batch.draw(texture, x + half, y, half, height);
        batch.setColor(Color.WHITE);

        drawLabel(batch, "Один", x, half, mode == GamePlayMode.SINGLE);
        drawLabel(batch, "С друзьями", x + half, half, mode == GamePlayMode.WITH_FRIENDS);
    }

    private void drawLabel(SpriteBatch batch, String label, float segX, float segW, boolean active) {
        GlyphLayout layout = new GlyphLayout(font, label);
        float tx = segX + (segW - layout.width) * 0.5f;
        float ty = y + (height + layout.height) * 0.5f;
        if (!active) {
            font.setColor(0.75f, 0.75f, 0.75f, 1f);
        }
        font.draw(batch, label, tx, ty);
        font.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
