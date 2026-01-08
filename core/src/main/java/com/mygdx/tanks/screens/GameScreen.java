package com.mygdx.tanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.GameSession;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.Tanks;
import com.mygdx.tanks.components.BackgroundView;
import com.mygdx.tanks.components.ButtonView;
import com.mygdx.tanks.components.VirtualJoystick;
import com.mygdx.tanks.managers.ContactManager;
import com.mygdx.tanks.objects.BulletObject;
import com.mygdx.tanks.objects.TankObject;
import com.mygdx.tanks.objects.WallsObject;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends ScreenAdapter {
    Tanks myGdxGame;

    TankObject tankObject;
    VirtualJoystick joystick;
    GameSession gameSession;
    ContactManager contactManager;

    ButtonView shootButton;
    BackgroundView backgroundView;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera gameCamera; // для мира
    private OrthographicCamera uiCamera;   // для UI
    private FitViewport worldViewport;
    private ScreenViewport uiViewport;

    ArrayList<BulletObject> bullets;
    ArrayList<WallsObject> walls;
    public ArrayList<WallsObject> wallsToDestroy;
    public ArrayList<TankObject> tanks;
    public ArrayList<Vector2> spawns;

    private int shootPointer = -1; // Отслеживание пальца, который нажал кнопку выстрела
    private int joystickPointer = -1; // Отслеживание пальца, который использует джойстик

    private static final int MAX_ENEMIES_ON_MAP = 4;
    private static final int TOTAL_ENEMIES = 10;

    private int enemiesSpawned = 0;
    private int enemiesKilled = 0;
    Texture spawnMarkerTexture;

    Random random;


    public GameScreen(Tanks myGdxGame) {
        this.myGdxGame = myGdxGame;
        contactManager = new ContactManager(myGdxGame.world, this);

        gameSession = new GameSession();

        backgroundView = new BackgroundView(GameResources.BACKGROUND_GAME_IMG_PATH);

        map = new TmxMapLoader().load("maps/map_2.tmx");  // карта
        renderer = new OrthogonalTiledMapRenderer(map);

        float mapWidth = GameSettings.MAP_WIDTH;
        float mapHeight = GameSettings.MAP_HEIGHT;

        random = new Random();

        spawnMarkerTexture = new Texture(GameResources.SPAWN_IMG_PATH);

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, mapWidth, mapHeight);
        gameCamera.position.set(mapWidth / 2, mapHeight / 2, 0);
        gameCamera.update();

        worldViewport = new FitViewport(mapWidth, mapHeight, gameCamera);
        worldViewport.apply(true);

        tankObject = new TankObject(
            200, 200,
            GameSettings.TANK_PIXEL_SIZE, GameSettings.TANK_PIXEL_SIZE,
            GameResources.TANK_IMG_PATH,
            myGdxGame.world, false
        );

        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);
        uiViewport.apply(true);

        joystick = new VirtualJoystick(
            uiViewport,
            300, 330,
            200, 130,
            GameResources.BACKGROUND_CONTROLLER_IMG_PATH,
            GameResources.CONTROLLER_IMG_PATH
        );

        shootButton = new ButtonView(
            1900, 200,
            300, 300,
            GameResources.SHOOT_BTN_IMG_PATH
        );
//        shootButton = new ButtonView(
//            700, 200,
//            300, 300,
//            GameResources.SHOOT_BTN_IMG_PATH
//        );

        bullets = new ArrayList<>();
        walls = new ArrayList<>();
        tanks = new ArrayList<>();
        spawns = new ArrayList<>();

        wallsToDestroy = new ArrayList<>();

        addMap();
        createWorldBounds();

        for (int i = 0; i < MAX_ENEMIES_ON_MAP; i++) {
            spawnEnemyIfPossible();
        }

    }

//    private void createEnemyTanks(int num) {
//        int delta = GameSettings.MAP_WIDTH / num;
//
//        for (int i = 0; i <= num; i++) {
//            int x = delta * i + (GameSettings.TANK_PIXEL_SIZE / 2) + 80;
//            int y = 1200;
//
//            TankObject tank = new TankObject(
//                x, y, GameSettings.TANK_PIXEL_SIZE, GameSettings.TANK_PIXEL_SIZE,
//                GameResources.ENEMY_TANK_IMG_PATH, myGdxGame.world, true
//            );
//
//            tanks.add(tank);
//        }
//    }

    private void addMap() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("1 layout");

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {

                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell == null) continue;

                TiledMapTile tile = cell.getTile();
                if (tile == null) continue;

                Object tileType = tile.getProperties().get("type");

                if (tileType == null) {
                    System.out.println("null");
                } else {
                    int worldX = x * GameSettings.TILE_SIZE + GameSettings.TILE_SIZE / 2;
                    int worldY = y * GameSettings.TILE_SIZE + GameSettings.TILE_SIZE / 2;

                    int type = 0;

                    switch (tileType.toString()){
                        case "1":
                            System.out.println("Brick");
                            type = GameSettings.TILE_BRICK;
                            break;
                        case "2":
                            System.out.println("Steel");
                            type = GameSettings.TILE_STEEL;
                            break;
                        case "3":
                            System.out.println("Water");
                            type = GameSettings.TILE_WATER;
                            break;
                        case "4":
                            System.out.println("Eagle");
                            type = GameSettings.TILE_EAGLE;
                            break;
                        case "5":
                            System.out.println("Forest");
                            type = GameSettings.TILE_FOREST;
                            break;
                        case "6":
                            System.out.println("Spawn");
                            type = GameSettings.TILE_SPAWN;
                            spawns.add(new Vector2(worldX, worldY));
                            break;
                    }

                    if (type == 0 || type == 6) continue;

                    WallsObject wallObject = new WallsObject(
                        worldX, worldY,
                        type,
                        myGdxGame.world
                    );

                    walls.add(wallObject);
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    private void spawnEnemyIfPossible() {
        if (enemiesSpawned >= TOTAL_ENEMIES) return;
        if (tanks.size() >= MAX_ENEMIES_ON_MAP) return;

        Vector2 coords = spawns.get(random.nextInt(spawns.size()));

        int x = (int) coords.x;
        int y = (int) coords.y;

        TankObject tank = new TankObject(
            x, y,
            GameSettings.TANK_PIXEL_SIZE,
            GameSettings.TANK_PIXEL_SIZE,
            GameResources.ENEMY_TANK_IMG_PATH,
            myGdxGame.world,
            true
        );

        tanks.add(tank);
        enemiesSpawned++;
    }

    public void render(float delta) {
        handleInput();

        joystick.update();
        updateBullets();
        updateWalls();
        updateEnemyTanks();

        myGdxGame.stepWorld();

        draw();
    }

    private void createWorldBounds() {
        float w = GameSettings.MAP_WIDTH;
        float h = GameSettings.MAP_HEIGHT;
        float thickness = 20f;

        createStaticWall(w / 2, -thickness / 2, w, thickness);       // низ
        createStaticWall(w / 2, h + thickness / 2, w, thickness);    // верх
        createStaticWall(-thickness / 2, h / 2, thickness, h);       // лево
        createStaticWall(w + thickness / 2, h / 2, thickness, h);    // право
    }

    private void createStaticWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(
            x * GameSettings.SCALE,
            y * GameSettings.SCALE
        );

        Body body = myGdxGame.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (width / 2f) * GameSettings.SCALE,
            (height / 2f) * GameSettings.SCALE
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("WORLD_BOUND");

        shape.dispose();
    }

    private void handleInput() {
        for (int i = 0; i < Gdx.input.getMaxPointers(); i++) {
            if (Gdx.input.isTouched(i)) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                uiViewport.unproject(touchPos);

                switch (gameSession.state) {
                    case PLAYING:
                        if (shootPointer == -1 && shootButton.isHit(touchPos.x, touchPos.y)) {
                            shootPointer = i;  // Запоминаем, какой палец нажал кнопку
                            System.out.println("Shoot button pressed!");
                        }

                        if (shootPointer == i) {
                            if (tankObject.canShoot()) {
                                tankObject.shoot();
                                System.out.println("Shoot!");

                                int x = tankObject.getX();
                                int y = tankObject.getY();

                                int offset = 45;

                                Vector2 bulletDir = new Vector2();
                                switch ((int) tankObject.angleDeg) {
                                    case 0:
                                    case 360:
                                        bulletDir.set(0, 1);
                                        y += offset;
                                        break;
                                    case 180:
                                        bulletDir.set(0, -1);
                                        y -= offset;
                                        break;
                                    case 90:
                                        bulletDir.set(-1, 0);
                                        x -= offset;
                                        break;
                                    case 270:
                                        bulletDir.set(1, 0);
                                        x += offset;
                                        break;
                                }

                                BulletObject bulletObject = new BulletObject(
                                    x, y, GameSettings.BULLET_PIXEL_SIZE, GameSettings.BULLET_PIXEL_SIZE,
                                    GameResources.BULLET_IMG_PATH, myGdxGame.world, bulletDir, tankObject, false
                                );

                                bullets.add(bulletObject);
                            }
                        }

                        if (joystickPointer == -1) {
                            joystickPointer = i;  // Запоминаем палец для джойстика
                            System.out.println("Joystick pressed!");
                        }

                        if (joystickPointer == i) {
                            Vector2 dir = joystick.getDirection();

                            if (dir.len() > 0) {
                                float absX = Math.abs(dir.x);
                                float absY = Math.abs(dir.y);

                                if (absX > absY) {
                                    dir.y = 0;
                                    dir.x = Math.signum(dir.x);
                                    tankObject.angleDeg = (dir.x > 0) ? 270 : 90;
                                } else {
                                    dir.x = 0;
                                    dir.y = Math.signum(dir.y);
                                    tankObject.angleDeg = (dir.y > 0) ? 0 : 180;
                                }

                                float speed = GameSettings.TANK_SPEED;
                                float deltaX = dir.x * speed * Gdx.graphics.getDeltaTime();
                                float deltaY = dir.y * speed * Gdx.graphics.getDeltaTime();
                                tankObject.move(dir);
                            } else {
                                tankObject.stop();
                            }
                        }
                        break;

                    case PAUSED:
                        System.out.println("Pause");
                        break;

                    case ENDED:
                        System.out.println("End");
                        break;
                }
            } else {
                // Когда палец отпустили, сбрасываем указатель
                if (shootPointer == i) {
                    shootPointer = -1;
                }
                if (joystickPointer == i) {
                    joystickPointer = -1;
                }
            }
        }
    }

    private void updateWalls() {
        for (int i = 0; i < walls.size(); i++) {
            if (walls.get(i).destroyed) {
                myGdxGame.world.destroyBody(walls.get(i).body);
                walls.remove(i--);
            }
        }
    }

    private void updateEnemyTanks() {
        for (int i = 0; i < tanks.size(); i++) {
            if (tanks.get(i).isEnemy() && !tanks.get(i).isDestroyed()) {
                tanks.get(i).enemyMove(tankObject);

                if (!tanks.get(i).isAlive()){
                    myGdxGame.world.destroyBody(tanks.get(i).body);
                    tanks.remove(i--);
                    enemiesKilled++;
                    continue;
                }

                if (tanks.get(i).canShoot()){
                    tanks.get(i).shoot();

                    int x = tanks.get(i).getX();
                    int y = tanks.get(i).getY();

                    int offset = 45;

                    Vector2 bulletDir = new Vector2();
                    switch ((int) tanks.get(i).angleDeg) {
                        case 0:
                        case 360:
                            bulletDir.set(0, 1);
                            y += offset;
                            break;
                        case 180:
                            bulletDir.set(0, -1);
                            y -= offset;
                            break;
                        case 90:
                            bulletDir.set(-1, 0);
                            x -= offset;
                            break;
                        case 270:
                            bulletDir.set(1, 0);
                            x += offset;
                            break;
                    }

                    BulletObject bulletObject = new BulletObject(
                        x, y, GameSettings.BULLET_PIXEL_SIZE, GameSettings.BULLET_PIXEL_SIZE,
                        GameResources.BULLET_IMG_PATH, myGdxGame.world, bulletDir, tanks.get(i), true
                    );

                    bullets.add(bulletObject);
                }
            }
        }
        spawnEnemyIfPossible();
    }

    private void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            if (bullets.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bullets.get(i).body);
                bullets.remove(i--);
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // МИР(объекты на карте и сама карта)
        worldViewport.apply();
        gameCamera.update();

//        renderer.setView(gameCamera);
//        renderer.render();

        myGdxGame.batch.setProjectionMatrix(gameCamera.combined);
        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);

        for (int i = 0; i < spawns.size(); i++) {
            float x = spawns.get(i).x;
            float y = spawns.get(i).y;

            myGdxGame.batch.draw(
                spawnMarkerTexture,
                x - 16, y - 16,
                32, 32
            );
        }

        tankObject.draw(myGdxGame.batch);
        for (TankObject tank : tanks) tank.draw(myGdxGame.batch);
        for (WallsObject wall : walls) wall.draw(myGdxGame.batch);
        for (BulletObject bullet : bullets) bullet.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        // UI и кнопки
        uiViewport.apply();
        uiCamera.update();

        myGdxGame.batch.setProjectionMatrix(uiCamera.combined);
        myGdxGame.batch.begin();

        joystick.draw(myGdxGame.batch);
        shootButton.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    @Override
    public void show() {
        restartGame();
    }

    private void restartGame() {
        if (tankObject != null) {
            myGdxGame.world.destroyBody(tankObject.body);
        }

        tankObject = new TankObject(
            200, 200,
            GameSettings.TANK_PIXEL_SIZE, GameSettings.TANK_PIXEL_SIZE,
            GameResources.TANK_IMG_PATH,
            myGdxGame.world, false
        );

        for (TankObject tank : tanks) {
            myGdxGame.world.destroyBody(tank.body);
        }

        for (BulletObject bullet : bullets) {
            myGdxGame.world.destroyBody(bullet.body);
        }

        bullets.clear();
        tanks.clear();

        for (int i = 0; i < MAX_ENEMIES_ON_MAP; i++) {
            spawnEnemyIfPossible();
        }

        gameSession.startGame();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        joystick.dispose();
        spawnMarkerTexture.dispose();
    }
}
