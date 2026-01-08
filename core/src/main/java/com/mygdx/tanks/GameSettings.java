package com.mygdx.tanks;

public class GameSettings {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    // Сетка уровня (в пикселях)
    public static final int TILE_SIZE = 64;
    public static final int MAP_WIDTH = 1472;
    public static final int MAP_HEIGHT = 1472;

    public static final float SCALE = 0.01f;

    // Физика
    public static final float STEP_TIME = 1f / 60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    // Скорости (в метрах/сек)
    public static final float TANK_SPEED = 2.3f;
    public static final float ENEMY_TANK_SPEED = 1.9f;
    public static final float BULLET_SPEED = 8.0f;

    // Размеры в пикселях
    public static final int TANK_PIXEL_SIZE = 56;
    public static final int BULLET_PIXEL_SIZE = 24;

    // Box2D категории
    public static final short TANK_BIT = 1;
    public static final short WATER_BIT = 0x0010;
    public static final short FOREST_BIT = 0;
    public static final short BULLET_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short STEEL_BIT = 8;
    public static final short EAGLE_BIT = 16;

    // Типы/виды тайлов
    public static final int TILE_EMPTY = 0;
    public static final int TILE_BRICK = 1;
    public static final int TILE_STEEL = 2;
    public static final int TILE_WATER = 3;
    public static final int TILE_EAGLE = 4;
    public static final int TILE_FOREST = 5;
    public static final int TILE_SPAWN = 6;

    // Параметры
    public static final int SHOOTING_COOL_DOWN = 500; // ms
    public static final int SHOOTING_COOL_DOWN_ENEMIES = 1500; // ms
    public static final int NEXT_MOVE = 3000;

    public static final float VISION_RADIUS = 700;   // радиус "зрения"
    public static final float AXIS_EPSILON = 20f;     // погрешность по оси

}
