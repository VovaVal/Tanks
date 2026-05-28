package com.mygdx.tanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.tanks.GamePlayMode;
import com.mygdx.tanks.LocalPlayerSlot;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.GameSession;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.MenuPlaySettings;
import com.mygdx.tanks.Effect;
import com.mygdx.tanks.GameState;
import com.mygdx.tanks.Tanks;
import com.mygdx.tanks.components.BackgroundView;
import com.mygdx.tanks.components.ButtonView;
import com.mygdx.tanks.components.ImageView;
import com.mygdx.tanks.components.LiveView;
import com.mygdx.tanks.components.TextView;
import com.mygdx.tanks.components.VirtualJoystick;
import com.mygdx.tanks.managers.ContactManager;
import com.mygdx.tanks.managers.MemoryManager;
import com.mygdx.tanks.objects.BonusObject;
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

    ShapeRenderer shapeRenderer;
    private float pauseHoldTimer = 0f;
    private static final float PAUSE_HOLD_DURATION = 3.0f;
    private int pausePointer = -1;

    ButtonView shootButton;
    BackgroundView backgroundView;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera gameCamera; // для мира
    private OrthographicCamera uiCamera;   // для UI
    private int backBufferW = 1;
    private int backBufferH = 1;
    /** Карта без искажения пропорций; поля вне мира — светло-серые (см. draw). */
    private static final float LETTERBOX_R = 0.88f;
    private static final float LETTERBOX_G = 0.88f;
    private static final float LETTERBOX_B = 0.90f;
    private FitViewport worldViewport;
    private StretchViewport uiViewport;

    public int tankLives = 3;

    private boolean isRestarting = false;

    ArrayList<BulletObject> bullets;
    ArrayList<WallsObject> walls;
    public ArrayList<WallsObject> wallsToDestroy;
    public ArrayList<TankObject> tanks;
    public ArrayList<Vector2> spawns;

    LiveView liveView;

    private int shootPointer = -1; // Отслеживание пальца, который нажал кнопку выстрела
    private int joystickPointer = -1; // Отслеживание пальца, который использует джойстик

    private static final int MAX_ENEMIES_ON_MAP = 4;
    private static final int TOTAL_ENEMIES = 10;

    private float respawnTimer = 0f;
    private static final float RESPAWN_DELAY = 2f;

    private int enemiesSpawned = 0;
    private int enemiesKilled = 0;
    Texture spawnMarkerTexture;
    Texture flag;

    Texture[] spawnFrames;
    ArrayList<Effect> spawnEffects;
    Texture[] deathFrames;
    ArrayList<Effect> deathEffects;

    Vector2 tankSpawnMain;

    Random random;

    private boolean playerDead = false;
    private boolean playerSpawning = false;

    TextView tanksKilledText;
    TextView tanksAllText;
    ImageView enemyTankImg;
    ImageView pauseBtnImg;

    ImageView fullBlackoutView;
    TextView pauseTextView;
    TextView loseWinTextView;
    ButtonView continueButton;
    ButtonView homeButton;

    ButtonView homeButton2;
    ButtonView continueButton2;

    Boolean restarting;
    public Boolean drawFlag = false;
    public long timeToDie;
    String mapPath;

    private float ignoreInputTimer = 0f;
    private static final float IGNORE_INPUT_TIME = 0.5f;
    private GameState previousState = GameState.PLAYING;
    private ArrayList<BonusObject> bonuses;
    private float bonusSpawnTimer = 0f;
    public boolean playerWin;

    public GamePlayMode playMode = GamePlayMode.SINGLE;
    public int friendPlayerCount = MenuPlaySettings.MIN_FRIEND_PLAYERS;

    private final ArrayList<LocalPlayerSlot> localPlayers = new ArrayList<>();
    private static final float MP_JOYSTICK_OUTER = 200f;
    private static final float MP_JOYSTICK_INNER = 130f;
    private static final float MP_JOYSTICK_Y = 240f;
    private static final float MP_JOYSTICK_Y_BOTTOM = 200f;
    private static final float MP_JOYSTICK_Y_TOP = GameSettings.UI_VIEWPORT_HEIGHT - 200f;


    public GameScreen(Tanks myGdxGame) {
        this.myGdxGame = myGdxGame;

        gameSession = new GameSession();
        gameSession.startGame();

        contactManager = new ContactManager(myGdxGame.world, this, myGdxGame.audioManager, gameSession);

        backgroundView = new BackgroundView(GameResources.BACKGROUND_GAME_IMG_PATH);

        shapeRenderer = new ShapeRenderer();

        map = new TmxMapLoader().load("maps/map_1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        float mapWidth = GameSettings.MAP_WIDTH;
        float mapHeight = GameSettings.MAP_HEIGHT;

        random = new Random();

        spawnMarkerTexture = new Texture(GameResources.SPAWN_IMG_PATH);
        flag = new Texture(GameResources.FLAG_IMG_PATH);

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, mapWidth, mapHeight);
        gameCamera.position.set(mapWidth / 2, mapHeight / 2, 0);
        gameCamera.update();

        worldViewport = new FitViewport(mapWidth, mapHeight, gameCamera);
        worldViewport.apply(true);

        uiCamera = new OrthographicCamera();
        uiViewport = new StretchViewport(GameSettings.UI_VIEWPORT_WIDTH, GameSettings.UI_VIEWPORT_HEIGHT, uiCamera);
        uiViewport.apply(true);

        int bw = Gdx.graphics.getBackBufferWidth();
        int bh = Gdx.graphics.getBackBufferHeight();
        if (bw > 0 && bh > 0) {
            resize(bw, bh);
        }

        joystick = new VirtualJoystick(
            uiViewport,
            320, 340,
            280, 182,
            GameResources.BACKGROUND_CONTROLLER_IMG_PATH,
            GameResources.CONTROLLER_IMG_PATH
        );

        final float shootBtnW = 400f;
        final float shootBtnH = 400f;
        final float shootMarginRight = 40f;
        shootButton = new ButtonView(
            GameSettings.UI_VIEWPORT_WIDTH - shootBtnW - shootMarginRight,
            200f,
            shootBtnW,
            shootBtnH,
            GameResources.SHOOT_BTN_IMG_PATH
        );

        tanksKilledText = new TextView(myGdxGame.largeWhiteFont, 0, 0, "0");
        tanksAllText = new TextView(myGdxGame.largeWhiteFont, 0, 0, " / " + Integer.toString(TOTAL_ENEMIES));
        enemyTankImg = new ImageView(0, 0, GameResources.ENEMY_TANK_IMG_PATH, 50, 50);
        final float pauseW = 280f;
        final float pauseH = 220f;
        final float pauseMarginTop = 20f;

        pauseBtnImg = new ImageView(
            0f,
            GameSettings.UI_VIEWPORT_HEIGHT - pauseH - pauseMarginTop,
            GameResources.PAUSE_BTN_IMG_PATH,
            (int) pauseW,
            (int) pauseH
        );

        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH,
            GameSettings.UI_VIEWPORT_WIDTH, GameSettings.UI_VIEWPORT_HEIGHT);

        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 0, 0, "Pause");
        loseWinTextView = new TextView(myGdxGame.largeWhiteFont, 0, 0, "");
        continueButton = new ButtonView(0, 0, 10, 10,
            myGdxGame.largeWhiteFont,
            GameResources.BUTTON_IMG_PATH,
            "Continue");
        homeButton = new ButtonView(0, 0, 10, 10,
            myGdxGame.largeWhiteFont,
            GameResources.BUTTON_IMG_PATH,
            "Home");

        homeButton2 = new ButtonView(0, 0, 10, 10,
            myGdxGame.largeWhiteFont,
            GameResources.BUTTON_IMG_PATH,
            "Home");
        continueButton2 = new ButtonView(0, 0, 10, 10,
            myGdxGame.largeWhiteFont,
            GameResources.BUTTON_IMG_PATH,
            "Play again");

        bullets = new ArrayList<>();
        walls = new ArrayList<>();
        tanks = new ArrayList<>();
        spawns = new ArrayList<>();
        wallsToDestroy = new ArrayList<>();
        bonuses = new ArrayList<>();

        spawnFrames = new Texture[4];
        spawnFrames[0] = new Texture("textures_imgs/spawn_1.png");
        spawnFrames[1] = new Texture("textures_imgs/spawn_2.png");
        spawnFrames[2] = new Texture("textures_imgs/spawn_3.png");
        spawnFrames[3] = new Texture("textures_imgs/spawn_4.png");

        spawnEffects = new ArrayList<>();

        deathEffects = new ArrayList<>();

        deathFrames = new Texture[3];
        deathFrames[0] = new Texture("textures_imgs/explosion_1.png");
        deathFrames[1] = new Texture("textures_imgs/explosion_2.png");
        deathFrames[2] = new Texture("textures_imgs/explosion_3.png");

        addMap();
        createWorldBounds();

        liveView = new LiveView(0, 0);

        if (!isFriendsMode()) {
            for (int i = 0; i < MAX_ENEMIES_ON_MAP; i++) {
                spawnEnemyIfPossible();
            }
            enemiesSpawned = 4;
        } else {
            enemiesSpawned = 0;
        }

        spawnPlayersForCurrentMode();

        enemiesKilled = 0;

        liveView.setLeftLives(tankLives);

        layoutTopRightHud();

        playerSpawning = false;
    }

    public boolean isFriendsMode() {
        return playMode == GamePlayMode.WITH_FRIENDS;
    }

    public void onFriendsPlayerTankLost() {
        tankLives = countAliveLocalPlayers();
        if (tankLives <= 0) {
            gameSession.state = GameState.ENDED;
            playerWin = false;
        }
    }

    private int countAliveLocalPlayers() {
        int alive = 0;
        for (LocalPlayerSlot slot : localPlayers) {
            if (slot.isAlive()) alive++;
        }
        return alive;
    }

    private void spawnPlayersForCurrentMode() {
        if (isFriendsMode()) {
            tankObject = null;
            disposeLocalPlayerJoysticks();
            localPlayers.clear();
            playerDead = false;
            playerSpawning = true;
            tankLives = friendPlayerCount;

            pauseBtnImg.width = 170;
            pauseBtnImg.height = 150;
            pauseBtnImg.x = GameSettings.UI_VIEWPORT_WIDTH - pauseBtnImg.width;
            pauseBtnImg.y = (GameSettings.UI_VIEWPORT_HEIGHT - pauseBtnImg.height) / 2f;

            for (int i = 0; i < friendPlayerCount; i++) {
                localPlayers.add(new LocalPlayerSlot(i));
                Vector2 pos = getMultiplayerSpawnPosition(i, friendPlayerCount);
                Effect effect = new Effect(spawnFrames, pos, 0.23f, GameSettings.TANK_PIXEL_SIZE);
                effect.isPlayerSpawn = true;
                effect.playerSpawnSlot = i;
                spawnEffects.add(effect);
            }
            setupMultiplayerJoysticks();
        } else {
            disposeLocalPlayerJoysticks();
            localPlayers.clear();
            playerSpawning = true;
            Effect effect = new Effect(spawnFrames, tankSpawnMain, 0.23f, GameSettings.TANK_PIXEL_SIZE);
            effect.isPlayerSpawn = true;
            effect.playerSpawnSlot = -1;
            spawnEffects.add(effect);
            tankLives = 3;

            final float pauseW = 280f;
            final float pauseH = 220f;
            final float pauseMarginTop = 20f;

            pauseBtnImg.width = pauseW;
            pauseBtnImg.height = pauseH;
            pauseBtnImg.x = 0f;
            pauseBtnImg.y = GameSettings.UI_VIEWPORT_HEIGHT - pauseH - pauseMarginTop;
        }
    }

    private Vector2 getMultiplayerSpawnPosition(int index, int total) {
        float offset = GameSettings.TILE_SIZE * 1.5f;

        float minX = offset;
        float maxX = GameSettings.MAP_WIDTH - offset;
        float minY = offset;
        float maxY = GameSettings.MAP_HEIGHT - offset;

        switch (index) {
            case 0:
                return new Vector2(minX, minY);
            case 1:
                return new Vector2(maxX, maxY);
            case 2:
                return new Vector2(minX, maxY);
            case 3:
                return new Vector2(maxX, minY);
            default:
                return tankSpawnMain.cpy();
        }
    }

    private void spawnTankForSlot(int slotIndex, Vector2 position) {
        String texturePath;
        switch (slotIndex) {
            case 1:  texturePath = GameResources.TANK_GREY_IMG_PATH; break;
            case 2:  texturePath = GameResources.TANK_RED_IMG_PATH; break;
            case 3:  texturePath = GameResources.TANK_GREEN_IMG_PATH; break;
            default: texturePath = GameResources.TANK_IMG_PATH; break;
        }

        TankObject playerTank = new TankObject(
            (int) position.x,
            (int) position.y,
            GameSettings.TANK_PIXEL_SIZE,
            GameSettings.TANK_PIXEL_SIZE,
            texturePath,
            myGdxGame.world,
            false,
            1
        );
        playerTank.angleDeg = 0;
        localPlayers.get(slotIndex).tank = playerTank;
        if (slotIndex == 0) {
            tankObject = playerTank;
        }
    }

    private void setupMultiplayerJoysticks() {
        int n = localPlayers.size();
        float vw = GameSettings.UI_VIEWPORT_WIDTH;

        float marginLeft = 220f;
        float marginRight = vw - 220f;

        for (int i = 0; i < n; i++) {
            LocalPlayerSlot slot = localPlayers.get(i);
            float centerX = 0f;
            float centerY = 0f;

            switch (i) {
                case 0:
                    centerX = marginLeft;
                    centerY = MP_JOYSTICK_Y_BOTTOM;
                    break;
                case 1:
                    centerX = marginRight;
                    if (n == 2) {
                        centerY = MP_JOYSTICK_Y_TOP;
                    } else {
                        centerY = MP_JOYSTICK_Y_BOTTOM;
                    }
                    break;
                case 2:
                    centerX = marginLeft;
                    centerY = MP_JOYSTICK_Y_TOP;
                    break;
                case 3:
                    centerX = marginRight;
                    centerY = MP_JOYSTICK_Y_TOP;
                    break;
            }

            if (slot.joystick == null) {
                String knobTexture;
                switch (i) {
                    case 1:  knobTexture = GameResources.CONTROLLER_IMG_PATH; break;
                    case 2:  knobTexture = GameResources.CONTROLLER_RED_IMG_PATH; break;
                    case 3:  knobTexture = GameResources.CONTROLLER_GREEN_IMG_PATH; break;
                    default: knobTexture = GameResources.CONTROLLER_YELLOW_IMG_PATH; break;
                }

                slot.joystick = new VirtualJoystick(
                    uiViewport,
                    centerX, centerY,
                    MP_JOYSTICK_OUTER, MP_JOYSTICK_INNER,
                    GameResources.BACKGROUND_CONTROLLER_IMG_PATH,
                    knobTexture // Применяем цвет
                );
            } else {
                slot.joystick.setCenter(centerX, centerY);
                slot.joystick.setRadii(MP_JOYSTICK_OUTER, MP_JOYSTICK_INNER);
            }
        }
    }

    private void disposeLocalPlayerJoysticks() {
        for (LocalPlayerSlot slot : localPlayers) {
            if (slot.joystick != null) {
                slot.joystick.dispose();
                slot.joystick = null;
            }
            slot.tank = null;
        }
        localPlayers.clear();
    }

    private void updateMultiplayerControls(float delta) {
        IntArray usedPointers = new IntArray();
        for (LocalPlayerSlot slot : localPlayers) {
            if (slot.joystick == null) continue;
            slot.joystick.update(usedPointers);
            int pointer = slot.joystick.getActivePointer();
            if (pointer >= 0) {
                usedPointers.add(pointer);
            }

            TankObject tank = slot.tank;
            if (tank == null || tank.isDestroyed() || !tank.isAlive()) {
                continue;
            }
            applyTankMovement(tank, slot.joystick.getDirection());
        }
    }

    private void updateFriendsAutoShoot(float delta) {
        for (LocalPlayerSlot slot : localPlayers) {
            TankObject tank = slot.tank;
            if (tank == null || tank.isDestroyed() || !tank.isAlive()) {
                continue;
            }
            slot.autoShootTimer += delta;
            if (slot.autoShootTimer >= GameSettings.FRIENDS_AUTO_SHOOT_INTERVAL_SEC) {
                slot.autoShootTimer = 0f;
                fireBulletFromTank(tank);
            }
        }
    }

    private void applyTankMovement(TankObject tank, Vector2 dir) {
        if (dir.len() > 0) {
            float absX = Math.abs(dir.x);
            float absY = Math.abs(dir.y);
            if (absX > absY) {
                dir.y = 0;
                dir.x = Math.signum(dir.x);
                tank.angleDeg = (dir.x > 0) ? 270 : 90;
            } else {
                dir.x = 0;
                dir.y = Math.signum(dir.y);
                tank.angleDeg = (dir.y > 0) ? 0 : 180;
            }
            tank.move(dir);
        } else {
            tank.stop();
        }
    }

    private void fireBulletFromTank(TankObject tank) {
        if (tank == null || tank.isDestroyed()) return;

        tank.shoot();
        myGdxGame.audioManager.shoot.play();

        int x = tank.getX();
        int y = tank.getY();
        int offset = 45;
        Vector2 bulletDir = new Vector2();
        switch ((int) tank.angleDeg) {
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
            default:
                bulletDir.set(0, 1);
                y += offset;
                break;
        }

        BulletObject bulletObject = new BulletObject(
            x, y, GameSettings.BULLET_PIXEL_SIZE, GameSettings.BULLET_PIXEL_SIZE,
            GameResources.BULLET_IMG_PATH, myGdxGame.world, bulletDir, tank, false
        );
        bullets.add(bulletObject);
    }

    public TankObject getLeadPlayerTank() {
        if (!isFriendsMode()) {
            return tankObject;
        }
        for (LocalPlayerSlot slot : localPlayers) {
            if (slot.isAlive()) {
                return slot.tank;
            }
        }
        return null;
    }

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
                            if (isFriendsMode()) {
                                continue;
                            }
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
                        case "7":
                            System.out.println("Spawn main");
                            tankSpawnMain = new Vector2(worldX, worldY);
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
        backBufferW = Math.max(1, width);
        backBufferH = Math.max(1, height);
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
        if (isFriendsMode() && !localPlayers.isEmpty()) {
            setupMultiplayerJoysticks();
        }
    }

    private void spawnEnemyIfPossible() {
        if (isFriendsMode()) return;

        if (enemiesSpawned >= TOTAL_ENEMIES) return;
        if (tanks.size() + spawnEffects.size() >= MAX_ENEMIES_ON_MAP) return;

        Vector2 coords = spawns.get(random.nextInt(spawns.size()));

        Effect effect = new Effect(spawnFrames, coords.cpy(), 0.23f, GameSettings.TANK_PIXEL_SIZE);
        spawnEffects.add(effect);

        enemiesSpawned++;
    }

    public void activateGrenade() {
        System.out.println("Grenade was activated!");

        int killedByGrenade = 0;

        for (TankObject enemy : tanks) {
            if (enemy.isEnemy() && !enemy.isDestroyed()) {
                enemy.markForDestroy();
                playDeath(enemy);
                killedByGrenade++;
            }
        }

        myGdxGame.audioManager.death.play();
    }


    private void playDeath(TankObject tank) {
        Vector2 coords = new Vector2((int) tank.getX(), (int) tank.getY());

        Effect effect = new Effect(deathFrames, coords.cpy(), 0.13f, GameSettings.TANK_PIXEL_SIZE);
        deathEffects.add(effect);

        if (tank.isEnemy()) {
            final int finalX = (int) coords.x;
            final int finalY = (int) coords.y;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    spawnBonusOnEnemyDeath(finalX, finalY);
                }
            });
        }
    }

    private void updateSpawnEffects(float delta) {
        for (int i = 0; i < spawnEffects.size(); i++) {
            Effect effect = spawnEffects.get(i);
            effect.update(delta);

            if (effect.finished) {

                if (effect.isPlayerSpawn) {
                    if (effect.playerSpawnSlot >= 0 && effect.playerSpawnSlot < localPlayers.size()) {
                        spawnTankForSlot(effect.playerSpawnSlot, effect.position);
                        if (effect.playerSpawnSlot == friendPlayerCount - 1) {
                            playerSpawning = false;
                        }
                    } else {
                        tankObject = new TankObject(
                            (int) effect.position.x,
                            (int) effect.position.y,
                            GameSettings.TANK_PIXEL_SIZE,
                            GameSettings.TANK_PIXEL_SIZE,
                            GameResources.TANK_IMG_PATH,
                            myGdxGame.world,
                            false,
                            1
                        );
                        tankObject.angleDeg = 0;
                        playerSpawning = false;
                    }
                    playerDead = false;
                } else {
                    TankObject enemy = new TankObject(
                        (int) effect.position.x,
                        (int) effect.position.y,
                        GameSettings.TANK_PIXEL_SIZE,
                        GameSettings.TANK_PIXEL_SIZE,
                        GameResources.ENEMY_TANK_IMG_PATH,
                        myGdxGame.world,
                        true,
                        2
                    );
                    tanks.add(enemy);
                }

                spawnEffects.remove(i--);
            }
        }
    }

    private void updateDeathEffects(float delta) {
        for (int i = 0; i < deathEffects.size(); i++) {
            Effect effect = deathEffects.get(i);
            effect.update(delta);

            if (effect.finished) {
                deathEffects.remove(i--);
            }
        }
    }

    @Override
    public void pause() {
        if (gameSession.state == GameState.PLAYING) {
            gameSession.pauseGame();
        }
    }

    public void render(float delta) {
        if (previousState != gameSession.state) {
            if (gameSession.state == GameState.ENDED ||
                gameSession.state == GameState.PAUSED) {
                shootPointer = -1;
                joystickPointer = -1;
                ignoreInputTimer = IGNORE_INPUT_TIME;
            }
            previousState = gameSession.state;
        }

        if (ignoreInputTimer > 0) {
            ignoreInputTimer -= delta;
        }

        handleInput();

        updateBonuses(delta);
        if (gameSession.state == GameState.PLAYING) {
            updateBonusTimers(delta);
            trySpawnRandomBonus();
        }

        if (gameSession.state == GameState.PLAYING) {
            if (pausePointer != -1) {
                if (Gdx.input.isTouched(pausePointer)) {
                    Vector3 touchPos = new Vector3(Gdx.input.getX(pausePointer), Gdx.input.getY(pausePointer), 0);
                    uiViewport.getCamera().unproject(touchPos);

                    if (touchPos.x >= pauseBtnImg.x && touchPos.x <= (pauseBtnImg.x + pauseBtnImg.width) &&
                        touchPos.y >= pauseBtnImg.y && touchPos.y <= (pauseBtnImg.y + pauseBtnImg.height)) {

                        pauseHoldTimer += delta;

                        if (pauseHoldTimer >= PAUSE_HOLD_DURATION) {
                            pauseHoldTimer = 0f;
                            pausePointer = -1;
                            gameSession.pauseGame();
                        }
                    } else {
                        pauseHoldTimer = 0f;
                        pausePointer = -1;
                    }
                } else {
                    pauseHoldTimer = 0f;
                    pausePointer = -1;
                }
            }
        } else {
            pauseHoldTimer = 0f;
            pausePointer = -1;
        }

        updateSpawnEffects(Gdx.graphics.getDeltaTime());
        updateDeathEffects(Gdx.graphics.getDeltaTime());
        if (!isFriendsMode()) {
            updatePlayerRespawn(Gdx.graphics.getDeltaTime());
        } else {
            updateFriendsDestroyedTanks();
        }

        if (!isFriendsMode() && tankLives == 0) {
            gameSession.state = GameState.ENDED;
            playerWin = false;
        }

        if (isFriendsMode() && !playerSpawning) {
            int alivePlayers = countAliveLocalPlayers();

            if (alivePlayers <= 1) {
                gameSession.state = GameState.ENDED;
                playerWin = true;
            }
        }

        if (drawFlag && timeToDie < TimeUtils.millis()) gameSession.state = GameState.ENDED;

        if (!isFriendsMode() && enemiesKilled >= TOTAL_ENEMIES){
            gameSession.state = GameState.ENDED;
            playerWin = true;

            Character c = mapPath.charAt(mapPath.length() - 5);
            System.out.println(c);

            if (c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
                int n = MemoryManager.getMaxLevel();

                if (c == Character.forDigit(n, 10)) {
                    MemoryManager.setMaxLevel(n + 1);
                }
            }
        }

        if (isFriendsMode()) {
            liveView.setLeftLives(countAliveLocalPlayers());
        } else {
            liveView.setLeftLives(tankLives);
        }
        tanksKilledText.setText(String.valueOf(enemiesKilled) + " ");
        if (gameSession.state == GameState.PLAYING) {
            if (isFriendsMode()) {
                updateMultiplayerControls(delta);
                updateFriendsAutoShoot(delta);
            } else if (!playerDead && tankObject != null) {
                joystick.update();
            }
        }
        updateBullets();
        updateWalls();
        if (gameSession.state == GameState.PLAYING) updateEnemyTanks();

        if (gameSession.state == GameState.PLAYING) myGdxGame.stepWorld();

        draw();
    }

    private void updateBonuses(float delta) {
        for (int i = 0; i < bonuses.size(); i++) {
            BonusObject bonus = bonuses.get(i);

            if (bonus.shouldRemove()) {
                myGdxGame.world.destroyBody(bonus.body);
                bonuses.remove(i--);
            }
        }
    }

    private void updateBonusTimers(float delta) {
        if (isFriendsMode()) {
            for (LocalPlayerSlot slot : localPlayers) {
                if (slot.tank != null) {
                    slot.tank.updateBonuses();
                }
            }
        } else if (tankObject != null) {
            tankObject.updateBonuses();
        }

        bonusSpawnTimer += delta;
    }

    private void trySpawnRandomBonus() {
        if (bonuses.size() >= GameSettings.MAX_BONUSES_ON_MAP) return;

        if (bonusSpawnTimer >= GameSettings.BONUS_SPAWN_INTERVAL) {
            spawnBonusAtRandomLocation();
            bonusSpawnTimer = 0f;
        }
    }

    private void spawnBonusAtRandomLocation() {
        int attempts = 0;
        while (attempts < 30) {
            int x = random.nextInt((int)GameSettings.MAP_WIDTH - 100) + 50;
            int y = random.nextInt((int)GameSettings.MAP_HEIGHT - 100) + 50;

            // Проверяем, что место свободно и не слишком близко к игроку
            if (isPositionFree(x, y) && !isTooCloseToPlayer(x, y)) {
                BonusObject.BonusType type = getRandomBonusType();
                BonusObject bonus = new BonusObject(x, y, type, myGdxGame.world);
                bonuses.add(bonus);
                System.out.println(getBonusName(type) + " created");
                break;
            }
            attempts++;
        }
    }

    private void spawnBonusOnEnemyDeath(int x, int y) {
        if (random.nextFloat() < GameSettings.BONUS_SPAWN_CHANCE) {
            BonusObject.BonusType type = getRandomBonusType();
            BonusObject bonus = new BonusObject(x, y, type, myGdxGame.world);
            bonuses.add(bonus);
            System.out.println(getBonusName(type));
        }
    }

    private boolean isPositionFree(float x, float y) {
        // Проверяем, нет ли на этой позиции стены
        for (WallsObject wall : walls) {
            float dx = Math.abs(wall.getX() - x);
            float dy = Math.abs(wall.getY() - y);
            if (dx < GameSettings.TILE_SIZE && dy < GameSettings.TILE_SIZE) {
                return false;
            }
        }
        return true;
    }

    private boolean isTooCloseToPlayer(float x, float y) {
        float minDistance = 200f;
        if (isFriendsMode()) {
            for (LocalPlayerSlot slot : localPlayers) {
                if (slot.tank == null) continue;
                float dx = Math.abs(slot.tank.getX() - x);
                float dy = Math.abs(slot.tank.getY() - y);
                if (dx < minDistance && dy < minDistance) return true;
            }
            return false;
        }
        if (tankObject == null) return false;

        float dx = Math.abs(tankObject.getX() - x);
        float dy = Math.abs(tankObject.getY() - y);
        return dx < minDistance && dy < minDistance;
    }

    private BonusObject.BonusType getRandomBonusType() {
        float rand = random.nextFloat();
        if (rand < 0.2f) return BonusObject.BonusType.SHIELD;
        else if (rand < 0.35f) return BonusObject.BonusType.SPEED;
        else if (rand < 0.5f) return BonusObject.BonusType.RAPID_FIRE;
        else if (rand < 0.65f) return BonusObject.BonusType.LIFE;
        else if (rand < 0.85f) return BonusObject.BonusType.FREEZE;
        else return BonusObject.BonusType.GRENADE;
    }

    private String getBonusName(BonusObject.BonusType type) {
        switch (type) {
            case SHIELD: return "Щит";
            case SPEED: return "Ускорение";
            case RAPID_FIRE: return "Ускоренная стрельба";
            case LIFE: return "+1 жизнь";
            case FREEZE: return "Заморозка";
            case GRENADE: return "Граната";
            default: return "Неизвестный";
        }
    }

    private void updateFriendsDestroyedTanks() {
        for (LocalPlayerSlot slot : localPlayers) {
            TankObject tank = slot.tank;
            if (tank == null || !tank.isDestroyed() || slot.deathProcessed) {
                continue;
            }
            slot.deathProcessed = true;
            tank.disablePhysics();
            playDeath(tank);
            if (tank.body != null) {
                myGdxGame.world.destroyBody(tank.body);
            }
            slot.tank = null;
        }
        tankObject = getLeadPlayerTank();
        tankLives = countAliveLocalPlayers();
    }

    private void updatePlayerRespawn(float delta) {
        if (isFriendsMode() || tankObject == null) return;

        if (tankObject.isDestroyed() && !playerDead) {
            playerDead = true;
            respawnTimer = 0f;

            tankObject.disablePhysics();

            playDeath(tankObject);

            myGdxGame.world.destroyBody(tankObject.body);

            if (tankLives > 0){
                Effect effect = new Effect(spawnFrames, tankSpawnMain, 0.23f, GameSettings.TANK_PIXEL_SIZE);
                effect.isPlayerSpawn = true;
                spawnEffects.add(effect);

                playerSpawning = true;
            }
        }

        if (playerDead && tankLives > 0) {
            respawnTimer += delta;

            if (respawnTimer >= RESPAWN_DELAY) {
                playerDead = false;
                respawnTimer = 0f;
            }
        }
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
        if (ignoreInputTimer > 0) return;

        for (int i = 0; i < Gdx.input.getMaxPointers(); i++) {
            if (Gdx.input.isTouched(i)) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                uiViewport.unproject(touchPos);

                switch (gameSession.state) {
                    case PLAYING:
//                        if (pauseBtnImg.isHit(touchPos.x, touchPos.y)) {
//                            myGdxGame.audioManager.btnClick.play();
//                            gameSession.pauseGame();
//                        }

                        if (touchPos.x >= pauseBtnImg.x && touchPos.x <= (pauseBtnImg.x + pauseBtnImg.width) &&
                            touchPos.y >= pauseBtnImg.y && touchPos.y <= (pauseBtnImg.y + pauseBtnImg.height)) {

                            if (pausePointer == -1) {
                                pausePointer = i;
                                pauseHoldTimer = 0f;
                            }
                        }

                        if (!isFriendsMode()) {
                            if (shootPointer == -1 && shootButton.isHit(touchPos.x, touchPos.y) && !playerDead) {
                                shootPointer = i;
                            }

                            if (shootPointer == i && !playerDead && tankObject != null) {
                                if (tankObject.canShoot()) {
                                    fireBulletFromTank(tankObject);
                                }
                            }

                            if (joystickPointer == -1) {
                                joystickPointer = i;
                            }

                            if (joystickPointer == i && !playerDead && tankObject != null) {
                                applyTankMovement(tankObject, joystick.getDirection());
                            }
                        }
                        break;

                    case PAUSED:
                        System.out.println("Pause");

                        if (continueButton.isHit(touchPos.x, touchPos.y)) {
                            gameSession.state = GameState.PLAYING;
                            myGdxGame.audioManager.btnClick.play();
                            for (TankObject tank: tanks) {
                                tank.lastShotTime = TimeUtils.millis();
                            }
                        } else if (homeButton.isHit(touchPos.x, touchPos.y)) {
                            gameSession.state = GameState.ENDED;
                            myGdxGame.audioManager.backgroundMusicGame.stop();
                            myGdxGame.audioManager.startSound.stop();
                            myGdxGame.audioManager.btnClick.play();
                            myGdxGame.setScreen(myGdxGame.homeScreen);
                        }
                        break;

                    case ENDED:
                        System.out.println("End");

                        if (continueButton2.isHit(touchPos.x, touchPos.y)) {
                            myGdxGame.audioManager.btnClick.play();
                            restart(mapPath);
                            return;
                        } else if (homeButton2.isHit(touchPos.x, touchPos.y)) {
                            gameSession.state = GameState.ENDED;
                            myGdxGame.audioManager.backgroundMusicGame.stop();
                            myGdxGame.audioManager.btnClick.play();
                            myGdxGame.setScreen(myGdxGame.homeScreen);
                        }
                        break;
                }
            } else {
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
            if (tanks.get(i).isPendingDestroy()) {
                if (tanks.get(i).body != null && tanks.get(i).body.getWorld() != null) {
                    myGdxGame.world.destroyBody(tanks.get(i).body);
                }
                tanks.remove(i);
                enemiesKilled++;
                continue;
            }

            if (tanks.get(i).isEnemy() && !tanks.get(i).isDestroyed() && tanks.get(i) != null) {
                TankObject lead = getLeadPlayerTank();
                if (lead != null && lead.isEnemyFrozen() && tanks.get(i) != null) {
                    tanks.get(i).stop();
                } else if (tanks.get(i) != null) {
                    tanks.get(i).enemyMove(lead, walls);
                }

                if (!tanks.get(i).isAlive()){
                    myGdxGame.audioManager.death.play();
                    playDeath(tanks.get(i));
                    myGdxGame.world.destroyBody(tanks.get(i).body);
                    tanks.remove(i--);
                    enemiesKilled++;
                    continue;
                }

                if (tanks.get(i).canShoot()) {
                    lead = getLeadPlayerTank();
                    if (lead != null && lead.isEnemyFrozen()) {
                        continue;
                    }
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

    /** Сердца и счётчик врагов — правый верхний угол виртуального HUD с отступом от краёв. */
    private void layoutTopRightHud() {
        if (liveView == null || tanksKilledText == null || tanksAllText == null || enemyTankImg == null) {
            return;
        }
        final float vw = GameSettings.UI_VIEWPORT_WIDTH;
        final float vh = GameSettings.UI_VIEWPORT_HEIGHT;
        final float pad = 40f;

        float heartsW = liveView.getHeartsClusterWidth();
        float heartH = liveView.getHeight();
        float liveX = vw - pad - heartsW;
        float liveY = vh - pad - heartH;
        liveView.setBounds(liveX, liveY, liveView.getWidth(), heartH);

        final float iconS = 50f;
        final float miniGap = 10f;
        final float blockGap = 24f;
        float twK = tanksKilledText.getWidth();
        float twA = tanksAllText.getWidth();
        float txH = Math.max(tanksKilledText.getHeight(), tanksAllText.getHeight());
        float rowW = iconS + miniGap + twK + twA;
        float iconX = liveX - blockGap - rowW;

        enemyTankImg.setBounds(iconX, liveY, iconS, iconS);

        float textY = liveY + (iconS - txH) * 0.5f;
        tanksKilledText.setBounds(iconX + iconS + miniGap, textY, twK, txH);
        tanksAllText.setBounds(iconX + iconS + miniGap + twK, textY, twA, txH);
    }

    /** Центр экрана: надпись Pause и кнопки в 2× размере (только экран паузы). */
    private void layoutPauseOverlay() {
        if (pauseTextView == null || continueButton == null || homeButton == null) {
            return;
        }
        BitmapFont font = myGdxGame.largeWhiteFont;
        float sx = font.getData().scaleX;
        float sy = font.getData().scaleY;
        font.getData().setScale(2f);
        pauseTextView.setText("Pause");

        final float vw = GameSettings.UI_VIEWPORT_WIDTH;
        final float vh = GameSettings.UI_VIEWPORT_HEIGHT;
        final float btnW = 1100f;
        final float btnH = 450f;
        final float btnGap = 48f;
        final float textGap = 48f;

        float tw = pauseTextView.getWidth();
        float th = pauseTextView.getHeight();
        float groupH = th + textGap + btnH;
        float groupBottom = (vh - groupH) * 0.5f;
        float btnY = groupBottom;

        float totalBtnsW = btnW + btnGap + btnW;
        float startX = (vw - totalBtnsW) * 0.5f;

        continueButton.setLayoutBounds(startX, btnY, btnW, btnH);
        homeButton.setLayoutBounds(startX + btnW + btnGap, btnY, btnW, btnH);
        pauseTextView.setBounds((vw - tw) * 0.5f, btnY + btnH + textGap, tw, th);

        font.getData().setScale(sx, sy);
    }

    /** Экран победы/поражения: крупные кнопки и надпись по центру (масштаб шрифта ×2). */
    private void layoutEndedOverlay() {
        if (loseWinTextView == null || continueButton2 == null || homeButton2 == null) {
            return;
        }
        BitmapFont font = myGdxGame.largeWhiteFont;
        float sx = font.getData().scaleX;
        float sy = font.getData().scaleY;
        font.getData().setScale(2f);
        loseWinTextView.setText(playerWin ? "YOU WON!" : "YOU LOST :(");

        final float vw = GameSettings.UI_VIEWPORT_WIDTH;
        final float vh = GameSettings.UI_VIEWPORT_HEIGHT;
        final float textGap = 56f;
        final float btnGap = 48f;

        float tw = loseWinTextView.getWidth();
        float th = loseWinTextView.getHeight();

        final float cw0 = 2000f;
        final float hw0 = 1200f;
        final float btnH0 = 600f;
        float rowW0 = cw0 + btnGap + hw0;
        float maxW = vw - 72f;
        float s = Math.min(1f, maxW / rowW0);
        float cw = cw0 * s;
        float hw = hw0 * s;
        float btnH = btnH0 * s;

        float groupH = th + textGap + btnH;
        float groupBottom = (vh - groupH) * 0.5f;
        float rowY = groupBottom;
        float rowW = cw + btnGap + hw;
        float startX = (vw - rowW) * 0.5f;

        continueButton2.setLayoutBounds(startX, rowY, cw, btnH);
        homeButton2.setLayoutBounds(startX + cw + btnGap, rowY, hw, btnH);
        loseWinTextView.setBounds((vw - tw) * 0.5f, rowY + btnH + textGap, tw, th);

        font.getData().setScale(sx, sy);
    }

    private void draw() {
        Gdx.gl.glViewport(0, 0, backBufferW, backBufferH);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // МИР: карта ровно по сетке, без растягивания по осям
        worldViewport.apply(true);
        gameCamera.update();

        myGdxGame.batch.setProjectionMatrix(gameCamera.combined);
        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);

        for (Effect effect: spawnEffects) {
            effect.draw(myGdxGame.batch);
        }

        for (Effect effect: deathEffects) {
            effect.draw(myGdxGame.batch);
        }

        if (isFriendsMode()) {
            for (LocalPlayerSlot slot : localPlayers) {
                if (slot.tank != null && !slot.tank.isDestroyed()) {
                    slot.tank.draw(myGdxGame.batch);
                }
            }
        } else if (tankObject != null && !tankObject.isDestroyed()) {
            tankObject.draw(myGdxGame.batch);
        }
        for (TankObject tank : tanks) tank.draw(myGdxGame.batch);
        for (WallsObject wall : walls)
            if (wall.getType() == GameSettings.TILE_EAGLE) {
                if (drawFlag) {
                    float x = wall.getX() - flag.getWidth() / 2f - 10;
                    float y = wall.getY() - flag.getHeight() / 2f - 10;
                    myGdxGame.batch.draw(flag, x, y, 64, 64);
                } else {
                    wall.draw(myGdxGame.batch);
                }
            } else {
                wall.draw(myGdxGame.batch);
            }

        for (BonusObject bonus : bonuses) {
            bonus.draw(myGdxGame.batch);
        }

        for (BulletObject bullet : bullets) bullet.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        // UI и кнопки
        uiViewport.apply();
        uiCamera.update();

        myGdxGame.batch.setProjectionMatrix(uiCamera.combined);
        if (gameSession.state == GameState.PAUSED) {
            layoutPauseOverlay();
        } else if (gameSession.state == GameState.ENDED && !isRestarting) {
            layoutEndedOverlay();
        }
        layoutTopRightHud();
        myGdxGame.batch.begin();

        if (isFriendsMode()) {
            for (LocalPlayerSlot slot : localPlayers) {
                if (slot.joystick != null) {
                    slot.joystick.draw(myGdxGame.batch);
                }
            }
        } else {
            joystick.draw(myGdxGame.batch);
            shootButton.draw(myGdxGame.batch);
        }

        if (!isFriendsMode()) {
            liveView.draw(myGdxGame.batch);
            tanksKilledText.draw(myGdxGame.batch);
            tanksAllText.draw(myGdxGame.batch);
            enemyTankImg.draw(myGdxGame.batch);
        }

        pauseBtnImg.draw(myGdxGame.batch);
        // ОТРИСОВКА КРУГА ПРОГРЕССА ПАУЗЫ
        if (pauseHoldTimer > 0f) {
            myGdxGame.batch.end();

            shapeRenderer.setProjectionMatrix(uiCamera.combined);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            float centerX = pauseBtnImg.x + pauseBtnImg.width / 2f;
            float centerY = pauseBtnImg.y + pauseBtnImg.height / 2f;
            float radius = Math.min(pauseBtnImg.width, pauseBtnImg.height) * 0.4f;

            float progress = pauseHoldTimer / PAUSE_HOLD_DURATION;
            float angle = progress * 360f;

            int segments = 40;

            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
            shapeRenderer.arc(centerX, centerY, radius, 0f, 360f, segments);

            shapeRenderer.setColor(1f, 0.3f, 0.3f, 0.8f);

            shapeRenderer.arc(centerX, centerY, radius, 90f, -angle, segments);

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            myGdxGame.batch.begin();
        }

        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            BitmapFont f = myGdxGame.largeWhiteFont;
            float osx = f.getData().scaleX;
            float osy = f.getData().scaleY;
            f.getData().setScale(2f);
            pauseTextView.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            f.getData().setScale(osx, osy);
        } else if (gameSession.state == GameState.ENDED && !isRestarting) {
            fullBlackoutView.draw(myGdxGame.batch);
            BitmapFont f = myGdxGame.largeWhiteFont;
            float osx = f.getData().scaleX;
            float osy = f.getData().scaleY;
            f.getData().setScale(2f);
            loseWinTextView.draw(myGdxGame.batch);
            continueButton2.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
            f.getData().setScale(osx, osy);
        }

        myGdxGame.batch.end();
    }

    @Override
    public void show() {
        int bw = Gdx.graphics.getBackBufferWidth();
        int bh = Gdx.graphics.getBackBufferHeight();
        if (bw > 0 && bh > 0) {
            resize(bw, bh);
        }
    }

    public void applyMenuSettings(MenuPlaySettings settings) {
        if (settings == null) return;
        playMode = settings.getMode();
        friendPlayerCount = settings.getFriendPlayerCount();
    }

    public void restart(String path) {
        System.out.println("Restarting game...");

        mapPath = path;

        myGdxGame.audioManager.backgroundMusicGame.stop();
        myGdxGame.audioManager.startSound.stop();

        disposeCurrentState();
        myGdxGame.resetWorld();

        gameSession = new GameSession();
        contactManager = new ContactManager(myGdxGame.world, this, myGdxGame.audioManager, gameSession);

        clearLogicalState();
        initializeGame(path);

        gameSession.startGame();
        myGdxGame.audioManager.startSound.play();
        myGdxGame.audioManager.backgroundMusicGame.play();
    }

    private void clearLogicalState() {
        bullets.clear();
        tanks.clear();
        walls.clear();
        spawnEffects.clear();
        deathEffects.clear();
        spawns.clear();
        wallsToDestroy.clear();

        tankObject = null;
        disposeLocalPlayerJoysticks();
        shootPointer = -1;
        joystickPointer = -1;

        drawFlag = false;
        timeToDie = 0;

        playerDead = false;
        playerSpawning = false;

        enemiesKilled = 0;
        enemiesSpawned = 0;
        tankLives = 3;
    }

    private void disposeCurrentState() {
        destroyAllBodies();

        if (bullets != null) bullets.clear();
        if (tanks != null) tanks.clear();
        if (walls != null) walls.clear();
        if (spawnEffects != null) spawnEffects.clear();
        if (deathEffects != null) deathEffects.clear();
        if (spawns != null) spawns.clear();
        if (bonuses != null) {
            for (BonusObject bonus : bonuses) {
                if (bonus != null && bonus.body != null) {
                    bonus.body.setUserData(null);
                }
            }
            bonuses.clear();
        }

        tankObject = null;
        disposeLocalPlayerJoysticks();
        shootPointer = -1;
        joystickPointer = -1;

        drawFlag = false;
        timeToDie = 0;

        playerDead = false;
        playerSpawning = false;
        enemiesKilled = 0;
        enemiesSpawned = 0;
        tankLives = 3;
    }

    private void destroyAllBodies() {
        try {
            com.badlogic.gdx.utils.Array<Body> bodies = new com.badlogic.gdx.utils.Array<>();
            myGdxGame.world.getBodies(bodies);

            for (Body body : bodies) {
                Fixture fixture = body.getFixtureList().first();
                if (fixture != null && "WORLD_BOUND".equals(fixture.getUserData())) {
                    continue;
                }
                myGdxGame.world.destroyBody(body);
            }
        } catch (Exception e) {
            System.err.println("Error destroying bodies: " + e.getMessage());
        }
    }

    private void initializeGame(String path) {
        map = new TmxMapLoader().load(path);
        renderer = new OrthogonalTiledMapRenderer(map);

        addMap();

        createWorldBounds();

        if (!isFriendsMode()) {
            for (int i = 0; i < MAX_ENEMIES_ON_MAP; i++) {
                spawnEnemyIfPossible();
            }
        }

        spawnPlayersForCurrentMode();

        liveView.setLeftLives(isFriendsMode() ? friendPlayerCount : tankLives);
        tanksKilledText.setText("0 ");
        if (isFriendsMode()) {
            setupMultiplayerJoysticks();
        }
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();

        disposeLocalPlayerJoysticks();
        if (joystick != null) joystick.dispose();
        if (shootButton != null) shootButton.dispose();
        if (backgroundView != null) backgroundView.dispose();
        if (liveView != null) liveView.dispose();
        if (pauseBtnImg != null) pauseBtnImg.dispose();
        if (enemyTankImg != null) enemyTankImg.dispose();
        if (fullBlackoutView != null) fullBlackoutView.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();

        for (Texture tex : spawnFrames) {
            if (tex != null) tex.dispose();
        }
        for (Texture tex : deathFrames) {
            if (tex != null) tex.dispose();
        }
        }
}
