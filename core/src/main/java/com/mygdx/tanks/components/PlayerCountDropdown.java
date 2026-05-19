package com.mygdx.tanks.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.MenuPlaySettings;

/**
 * Выпадающий список числа игроков (2–4) для режима «с друзьями».
 * Раскрывается сверху вниз.
 */
public class PlayerCountDropdown extends View {

    private final Texture texture;
    private final BitmapFont font;
    private int selectedCount = MenuPlaySettings.MIN_FRIEND_PLAYERS;
    private boolean expanded;

    public PlayerCountDropdown(BitmapFont font) {
        super(0, 0);
        this.font = font;
        texture = new Texture(GameResources.BUTTON_IMG_PATH);
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(int count) {
        if (count >= MenuPlaySettings.MIN_FRIEND_PLAYERS && count <= MenuPlaySettings.MAX_FRIEND_PLAYERS) {
            selectedCount = count;
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public float getCollapsedHeight() {
        return height;
    }

    public float getTotalHeight() {
        if (!expanded) return height;
        // Заголовок + количество доступных опций выбора
        int optionsCount = MenuPlaySettings.MAX_FRIEND_PLAYERS - MenuPlaySettings.MIN_FRIEND_PLAYERS + 1;
        return height * (1 + optionsCount);
    }

    @Override
    public boolean isHit(float tx, float ty) {
        // Так как шторка идет ВНИЗ, общая зона клика теперь простирается от (y - общей_высоты + высота_шапки) до (y + высота_шапки)
        float totalH = getTotalHeight();
        float minY = (y + height) - totalH;
        float maxY = y + height;
        return tx >= x && tx <= x + width && ty >= minY && ty <= maxY;
    }

    /** Клик по заголовку (свернуть/развернуть): 0. По пункту: 2, 3 или 4. Иначе -1. */
    public int handleTouch(float tx, float ty) {
        if (!isHit(tx, ty)) return -1;

        float headerMinY = y;
        float headerMaxY = y + height;

        // Клик по главной кнопке (заголовку)
        if (ty >= headerMinY && ty <= headerMaxY) {
            expanded = !expanded;
            return 0;
        }

        if (!expanded) return -1;

        // Клик по выпадающим элементам (они находятся ниже основной кнопки y)
        // Считаем индекс сверху вниз (первый элемент под кнопкой имеет индекс 0)
        int index = (int) ((headerMinY - ty) / height);
        int value = MenuPlaySettings.MIN_FRIEND_PLAYERS + index;

        if (value >= MenuPlaySettings.MIN_FRIEND_PLAYERS && value <= MenuPlaySettings.MAX_FRIEND_PLAYERS) {
            selectedCount = value;
            expanded = false;
            return value;
        }
        return -1;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (width <= 0 || height <= 0) return;

        String header = selectedCount + (expanded ? "" : "");
        float totalH = getTotalHeight();

        batch.setColor(0.12f, 0.12f, 0.16f, 0.85f);
        float backgroundY = (y + height) - totalH;
        batch.draw(texture, x - 6, backgroundY - 6, width + 12, totalH + 12);
        batch.setColor(Color.WHITE);

        batch.draw(texture, x, y, width, height);
        drawCenteredText(batch, header, y, height, true);

        if (!expanded) return;

        // Рисуем элементы списка, уходящие вниз под главную кнопку
        int count = 0;
        for (int p = MenuPlaySettings.MIN_FRIEND_PLAYERS; p <= MenuPlaySettings.MAX_FRIEND_PLAYERS; p++) {
            count++;
            // Каждый следующий элемент смещается ниже по оси Y
            float rowY = y - (height * count);

            float alpha = p == selectedCount ? 1f : 0.55f;
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(texture, x, rowY, width, height);
            batch.setColor(Color.WHITE);

            drawCenteredText(batch, String.valueOf(p), rowY, height, p == selectedCount);
        }
    }

    private void drawCenteredText(SpriteBatch batch, String text, float rowY, float rowH, boolean bright) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float tx = x + (width - layout.width) * 0.5f;
        float ty = rowY + (rowH + layout.height) * 0.5f;
        if (!bright) font.setColor(0.8f, 0.8f, 0.8f, 1f);

        font.draw(batch, text, tx, ty);
        font.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
