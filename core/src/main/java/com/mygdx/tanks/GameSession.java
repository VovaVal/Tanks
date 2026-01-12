package com.mygdx.tanks;

import com.badlogic.gdx.utils.TimeUtils;

public class GameSession {
    public static GameState state;
    long sessionStartTime;
    long pauseStartTime;

    private int destructedTankNumber;

    public GameSession() {
    }

    public void startGame() {
        state = GameState.PLAYING;
        destructedTankNumber = 0;
        sessionStartTime = TimeUtils.millis();
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }
}
