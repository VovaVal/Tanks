package com.mygdx.tanks.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.GameResources;

public class BonusObject extends GameObject {

    public enum BonusType {
        SHIELD,        // Щит - непробиваемая броня
        SPEED,         // Ускорение движения
        RAPID_FIRE,    // Ускорение стрельбы
        LIFE,          // +1 жизнь
        FREEZE,        // Заморозка врагов
        GRENADE        // Уничтожение всех врагов
    }

    private BonusType type;
    private boolean active = true;
    private long spawnTime;
    private static final long LIFE_TIME = 8000;

    public BonusObject(int x, int y, BonusType type, World world) {
        super(
            getTexturePath(type),
            x, y,
            GameSettings.TILE_SIZE,
            GameSettings.TILE_SIZE,
            GameSettings.BONUS_BIT,
            world,
            "bonus"
        );

        this.type = type;
        this.spawnTime = TimeUtils.millis();

        body.getFixtureList().first().setSensor(true);
    }

    private static String getTexturePath(BonusType type) {
        switch (type) {
            case SHIELD:
                return GameResources.SHIELD_BONUS_IMG_PATH;
            case SPEED:
                return GameResources.SPEED_BONUS_IMG_PATH;
            case RAPID_FIRE:
                return GameResources.RAPID_FIRE_BONUS_IMG_PATH;
            case LIFE:
                return GameResources.LIFE_BONUS_IMG_PATH;
            case FREEZE:
                return GameResources.FREEZE_BONUS_IMG_PATH;
            case GRENADE:
                return GameResources.GRENADE_BONUS_IMG_PATH;
            default:
                return GameResources.SHIELD_BONUS_IMG_PATH;
        }
    }

    public BonusType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public boolean shouldRemove() {
        return !active || TimeUtils.millis() - spawnTime > LIFE_TIME;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (active) {
            long timeLeft = LIFE_TIME - (TimeUtils.millis() - spawnTime);
            if (timeLeft > 2000 || (timeLeft / 200) % 2 == 0) {
                float floatOffset = (float)Math.sin(TimeUtils.millis() * 0.003) * 3;
                batch.draw(texture,
                    getX() - width/2f,
                    getY() - height/2f + floatOffset,
                    width, height);
            }
        }
    }
}
