package com.mygdx.tanks.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class MemoryManager {
    private static final String MAX_LEVEL_KEY = "max_level";

    private static final Preferences preferences = Gdx.app.getPreferences("User saves");

    public static int getMaxLevel() {
        return preferences.getInteger(MAX_LEVEL_KEY, 3);
    }

    public static void setMaxLevel(int level) {
        preferences.putInteger(MAX_LEVEL_KEY, level);
        preferences.flush();
    }
}
