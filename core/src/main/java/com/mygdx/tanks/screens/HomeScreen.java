package com.mygdx.tanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.tanks.GameResources;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.Tanks;
import com.mygdx.tanks.GamePlayMode;
import com.mygdx.tanks.components.BackgroundView;
import com.mygdx.tanks.components.ButtonView;
import com.mygdx.tanks.components.ImageView;
import com.mygdx.tanks.components.ModeToggleView;
import com.mygdx.tanks.components.PlayerCountDropdown;
import com.mygdx.tanks.managers.MemoryManager;

public class HomeScreen extends ScreenAdapter {
    Tanks myGdxGame;
    ButtonView level1;
    ButtonView level2;
    ButtonView level3;
    ButtonView level4;
    ButtonView level5;
    ButtonView level6;
    ButtonView level7;
    ButtonView level8;
    ButtonView level9;
    ButtonView level10;
    BackgroundView homeBackground;
    ImageView tanksText;
    ImageView chains5;
    ImageView chains4;
    ImageView chains6;
    ImageView chains7;
    ImageView chains8;
    ImageView chains9;
    ImageView chains10;
    private StretchViewport uiViewport;
    GameScreen gameScreen;

    private ModeToggleView modeToggle;
    private PlayerCountDropdown playerCountDropdown;

    public HomeScreen(Tanks myGdxGame) {
        this.myGdxGame = myGdxGame;

        gameScreen = new GameScreen(this.myGdxGame);

        uiViewport = new StretchViewport(GameSettings.UI_VIEWPORT_WIDTH, GameSettings.UI_VIEWPORT_HEIGHT);
        uiViewport.apply(true);

        homeBackground = new BackgroundView(
            GameResources.HOME_BACKGROUND_IMG_PATH,
            GameSettings.UI_VIEWPORT_WIDTH,
            GameSettings.UI_VIEWPORT_HEIGHT
        );

        level1 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 1");
        level2 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 2");
        level3 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 3");
        level4 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 4");
        level5 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 5");
        level6 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 6");
        level7 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 7");
        level8 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 8");
        level9 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 9");
        level10 = new ButtonView(0, 0, 10, 10, myGdxGame.largeWhiteFont, GameResources.BUTTON_IMG_PATH, "level 10");

        tanksText = new ImageView(0, 0, GameResources.TANKS_TEXT_IMG_PATH, 10, 10);

        chains4 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);
        chains5 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);
        chains6 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);
        chains7 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);
        chains8 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);
        chains9 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);
        chains10 = new ImageView(0, 0, GameResources.CHAINS_IMG_PATH, 10, 10);

        modeToggle = new ModeToggleView(myGdxGame.largeWhiteFont);
        modeToggle.setMode(myGdxGame.menuPlaySettings.getMode());

        // ПРИМЕЧАНИЕ: Если ваш ModeToggleView поддерживает изменение текста динамически,
        // вы можете раскомментировать или добавить метод инициализации строк ниже:
        // modeToggle.setLabels("Соло", "Друг");

        playerCountDropdown = new PlayerCountDropdown(myGdxGame.largeWhiteFont);
        playerCountDropdown.setSelectedCount(myGdxGame.menuPlaySettings.getFriendPlayerCount());

        layoutMenu();
    }

    private void layoutMenu() {
        final float vw = GameSettings.UI_VIEWPORT_WIDTH;
        final float vh = GameSettings.UI_VIEWPORT_HEIGHT;

        final int cols = 5;
        final float marginX = vw * 0.035f;
        final float marginY = vh * 0.045f;
        final float gapX = vw * 0.008f;
        final float rowGap = vh * 0.026f;
        final float titleGap = vh * 0.001f;

        float availW = vw - 2f * marginX;
        float buttonW = (availW - gapX * (cols - 1)) / cols;
        float buttonH = Math.min(vh * 0.31f, buttonW * 0.74f);
        buttonH = Math.max(buttonH, vh * 0.145f);

        float gridW = cols * buttonW + (cols - 1) * gapX;
        float gridH = 1.7f * buttonH + rowGap;

        final float titleAspect = 700f / 600f;
        float titleW = Math.min(vw * 0.64f, gridW * 1.14f);
        float titleH = titleW / titleAspect;
        if (titleH > vh * 0.5f) {
            titleH = vh * 0.5f;
            titleW = titleH * titleAspect;
        }

        final float toggleW = Math.min(vw * 0.32f, 850f);
        final float toggleH = Math.max(vh * 0.13f, 130f);

        final float dropdownW = Math.min(vw * 0.23f, 420f);
        final float dropdownH = Math.max(vh * 0.085f, 90f);

        float modeBlockH = Math.max(toggleH, dropdownH);
        final float reservedTop = modeBlockH + marginY * 1.6f;

        float contentH = titleH + titleGap + gridH;
        float availH = vh - reservedTop - marginY;
        float rowBottomY = Math.max(marginY, marginY + (availH - contentH) * 0.5f);
        float rowTopY = rowBottomY + buttonH + rowGap;

        final float logoDownShift = vh * 0.055f;
        float titleY = rowTopY + buttonH + titleGap - logoDownShift;
        float titleX = (vw - titleW) * 0.5f;

        tanksText.setBounds(titleX, titleY, titleW, titleH);

        float gridOriginX = (vw - gridW) * 0.5f;

        ButtonView[] topRow = {level1, level2, level3, level4, level5};
        ButtonView[] bottomRow = {level6, level7, level8, level9, level10};
        for (int i = 0; i < cols; i++) {
            float bx = gridOriginX + i * (buttonW + gapX);
            bottomRow[i].setLayoutBounds(bx, rowBottomY, buttonW, buttonH);
            topRow[i].setLayoutBounds(bx, rowTopY, buttonW, buttonH);
        }

        float chainW = Math.min(buttonW * 0.86f, vw * 0.15f);
        float chainH = chainW * (155f / 400f);
        ImageView[] chains = {chains4, chains5, chains6, chains7, chains8, chains9, chains10};
        for (int i = 0; i < chains.length; i++) {
            int level = 4 + i;
            ButtonView btn = level <= 5 ? topRow[level - 1] : bottomRow[level - 6];
            float cx = btn.x + btn.width * 0.5f - chainW * 0.5f;
            float cy = btn.y + btn.height * 0.62f - chainH * 0.55f;
            chains[i].setBounds(cx, cy, chainW, chainH);
        }

        // Вызываем обновленный метод позиционирования по разным сторонам
        layoutModeControlsInCorners(vw, vh, marginY, toggleW, toggleH, dropdownW, dropdownH);
    }

    /** Разносит элементы управления: Тип игры — СЛЕВА, Количество игроков — СПРАВА */
    private void layoutModeControlsInCorners(float vw, float vh, float marginY,
                                             float toggleW, float toggleH, float dropdownW, float dropdownH) {

        // 1. Кнопки выбора типа игры уходят строго влево
        float leftX = vw * 0.04f;
        float toggleY = vh - marginY - toggleH;
        modeToggle.setBounds(leftX, toggleY, toggleW, toggleH);

        // 2. Выпадающий список количества игроков уходит строго вправо
        if (myGdxGame.menuPlaySettings.isWithFriends()) {
            float rightX = vw - (vw * 0.04f) - dropdownW;
            float dropdownY = vh - marginY - dropdownH;
            playerCountDropdown.setBounds(rightX, dropdownY, dropdownW, dropdownH);
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
        layoutMenu();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        uiViewport.unproject(touchPos);

        if (handleModeControlsInput(touchPos.x, touchPos.y)) {
            return;
        }

        if (level1.isHit(touchPos.x, touchPos.y)) {
            startLevel("maps/map_1.tmx");
        } else if (level2.isHit(touchPos.x, touchPos.y)) {
            startLevel("maps/map_2.tmx");
        } else if (level3.isHit(touchPos.x, touchPos.y)) {
            startLevel("maps/map_3.tmx");
        } else if (level4.isHit(touchPos.x, touchPos.y) && 4 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_4.tmx");
        } else if (level5.isHit(touchPos.x, touchPos.y) && 5 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_5.tmx");
        } else if (level6.isHit(touchPos.x, touchPos.y) && 6 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_6.tmx");
        } else if (level7.isHit(touchPos.x, touchPos.y) && 7 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_7.tmx");
        } else if (level8.isHit(touchPos.x, touchPos.y) && 8 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_8.tmx");
        } else if (level9.isHit(touchPos.x, touchPos.y) && 9 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_9.tmx");
        } else if (level10.isHit(touchPos.x, touchPos.y) && 10 <= MemoryManager.getMaxLevel()) {
            startLevel("maps/map_10.tmx");
        }
    }

    private boolean handleModeControlsInput(float tx, float ty) {
        if (myGdxGame.menuPlaySettings.isWithFriends() && playerCountDropdown.isHit(tx, ty)) {
            playerCountDropdown.handleTouch(tx, ty);
            myGdxGame.menuPlaySettings.setFriendPlayerCount(playerCountDropdown.getSelectedCount());
            myGdxGame.audioManager.btnClick.play();
            layoutMenu();
            return true;
        }

        if (modeToggle.isHit(tx, ty)) {
            int seg = modeToggle.hitSegment(tx, ty);
            if (seg >= 0) {
                GamePlayMode next = seg == 0 ? GamePlayMode.SINGLE : GamePlayMode.WITH_FRIENDS;
                if (next != modeToggle.getMode()) {
                    modeToggle.setMode(next);
                    myGdxGame.menuPlaySettings.setMode(next);
                    if (next == GamePlayMode.WITH_FRIENDS) {
                        playerCountDropdown.setExpanded(false);
                    }
                    layoutMenu();
                }
                myGdxGame.audioManager.btnClick.play();
            }
            return true;
        }

        return false;
    }

    private void startLevel(String mapPath) {
        if (playerCountDropdown != null) {
            playerCountDropdown.setExpanded(false);
        }

        syncMenuSettingsFromUi();
        gameScreen.applyMenuSettings(myGdxGame.menuPlaySettings);
        myGdxGame.audioManager.btnClick.play();
        gameScreen.restart(mapPath);
        myGdxGame.setScreen(gameScreen);
    }

    private void syncMenuSettingsFromUi() {
        myGdxGame.menuPlaySettings.setMode(modeToggle.getMode());
        myGdxGame.menuPlaySettings.setFriendPlayerCount(playerCountDropdown.getSelectedCount());
    }

    private void draw() {
        ScreenUtils.clear(Color.CLEAR);
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiViewport.apply();
        myGdxGame.batch.setProjectionMatrix(uiViewport.getCamera().combined);

        myGdxGame.batch.begin();

        homeBackground.draw(myGdxGame.batch);

        level1.draw(myGdxGame.batch);
        level2.draw(myGdxGame.batch);
        level3.draw(myGdxGame.batch);
        level4.draw(myGdxGame.batch);
        level5.draw(myGdxGame.batch);
        level6.draw(myGdxGame.batch);
        level7.draw(myGdxGame.batch);
        level8.draw(myGdxGame.batch);
        level9.draw(myGdxGame.batch);
        level10.draw(myGdxGame.batch);

        tanksText.draw(myGdxGame.batch);

        if(!(4 <= MemoryManager.getMaxLevel())) chains4.draw(myGdxGame.batch);
        if(!(5 <= MemoryManager.getMaxLevel())) chains5.draw(myGdxGame.batch);
        if(!(6 <= MemoryManager.getMaxLevel())) chains6.draw(myGdxGame.batch);
        if(!(7 <= MemoryManager.getMaxLevel())) chains7.draw(myGdxGame.batch);
        if(!(8 <= MemoryManager.getMaxLevel())) chains8.draw(myGdxGame.batch);
        if(!(9 <= MemoryManager.getMaxLevel())) chains9.draw(myGdxGame.batch);
        if(!(10 <= MemoryManager.getMaxLevel())) chains10.draw(myGdxGame.batch);

        if (modeToggle != null) {
            modeToggle.draw(myGdxGame.batch);
        }
        if (myGdxGame.menuPlaySettings.isWithFriends() && playerCountDropdown != null) {
            playerCountDropdown.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();
    }

    @Override
    public void show() {
        int bw = Gdx.graphics.getBackBufferWidth();
        int bh = Gdx.graphics.getBackBufferHeight();
        if (bw > 0 && bh > 0) {
            resize(bw, bh);
        } else {
            layoutMenu();
        }
        myGdxGame.audioManager.menuMusicSound.play();
    }

    @Override
    public void hide() {
        myGdxGame.audioManager.menuMusicSound.stop();
    }

    @Override
    public void dispose() {
        if (homeBackground != null) homeBackground.dispose();

        if (level1 != null) level1.dispose();
        if (level2 != null) level2.dispose();
        if (level3 != null) level3.dispose();
        if (level4 != null) level4.dispose();
        if (level5 != null) level5.dispose();
        if (level6 != null) level6.dispose();
        if (level7 != null) level7.dispose();
        if (level8 != null) level8.dispose();
        if (level9 != null) level9.dispose();
        if (level10 != null) level10.dispose();

        if (tanksText != null) tanksText.dispose();
        if (modeToggle != null) modeToggle.dispose();
        if (playerCountDropdown != null) playerCountDropdown.dispose();
    }
}
