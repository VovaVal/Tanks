package com.mygdx.tanks.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.GameSettings;

import java.util.Random;

public class WallsObject extends GameObject {
    private final int tileType;
    public boolean destroyed = false;
    private Texture destroyedTexture;

    public WallsObject(int x, int y, int tileType, World world) {
        super(
            getTexturePath(tileType),
            x, y,
            GameSettings.TILE_SIZE,
            GameSettings.TILE_SIZE,
            getCategoryBits(tileType),
            world,
            "wall"
        );

        this.tileType = tileType;
        destroyedTexture = new Texture("textures_imgs/22.png");
    }

    private static String getTexturePath(int tileType) {
        switch (tileType) {
            case GameSettings.TILE_BRICK:
                return GameResources.BRICK_IMG_PATH;
            case GameSettings.TILE_STEEL:
                return GameResources.STEEL_IMG_PATH;
            case GameSettings.TILE_EAGLE:
                return GameResources.EAGLE_IMG_PATH;
            case GameSettings.TILE_WATER:
                return GameResources.WATER_IMG_PATH;
            case GameSettings.TILE_FOREST:
                return GameResources.FOREST_IMG_PATH;
            default:
                return GameResources.BRICK_IMG_PATH;
        }
    }

    public static short getCategoryBits(int tileType) {
        switch (tileType) {
            case GameSettings.TILE_BRICK:
                return GameSettings.BRICK_BIT;
            case GameSettings.TILE_STEEL:
                return GameSettings.STEEL_BIT;
            case GameSettings.TILE_EAGLE:
                return GameSettings.EAGLE_BIT;
            case GameSettings.TILE_WATER:
                return GameSettings.WATER_BIT;
            case GameSettings.TILE_FOREST:
                return GameSettings.FOREST_BIT;
            default:
                return GameSettings.BRICK_BIT;
        }
    }

    public int getType() {
        return tileType;
    }

    public boolean isDestructible() {
        return tileType == GameSettings.TILE_BRICK;
    }

    public boolean isEagle() {
        return tileType == GameSettings.TILE_EAGLE;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!destroyed) {
            batch.draw(texture, getX() - width/2f, getY() - height/2f, width, height);
        } else {
            batch.draw(destroyedTexture, getX() - width/2f, getY() - height/2f, width, height);
        }
    }

    public void destroy() {
        destroyed = true;
    }
}
