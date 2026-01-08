package com.mygdx.tanks;

import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.tanks.managers.MemoryManager;

public class GameSession {
    public static GameState state;
    long nextTankSpawnTime;
    long sessionStartTime;
    long pauseStartTime;

    private int destructedTankNumber;

    public GameSession() {
    }

    public void destructionRegistration() {
        destructedTankNumber += 1;
    }

    public int getDestructedTankNumber() {
        return destructedTankNumber;
    }

    public void startGame() {
        state = GameState.PLAYING;
        destructedTankNumber = 0;
        sessionStartTime = TimeUtils.millis();
//        nextTrashSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
//            * getTrashPeriodCoolDown());
    }

//    public boolean shouldSpawnTrash() {
//        if (nextTrashSpawnTime <= TimeUtils.millis()) {
//            nextTrashSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
//                * getTrashPeriodCoolDown());
//            return true;
//        }
//        return false;
//    }

    private float getTrashPeriodCoolDown() {
        return (float) Math.exp(-0.001 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }

    public void endGame() {
        state = GameState.ENDED;
        destructedTankNumber = 0;
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        sessionStartTime += TimeUtils.millis() - pauseStartTime;
    }
}
