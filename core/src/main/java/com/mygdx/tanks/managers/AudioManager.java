package com.mygdx.tanks.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.tanks.GameResources;

public class AudioManager {

    public Music backgroundMusicGame;
    public Sound shoot;
    public Sound death;
    public Sound startSound;
    public Sound hitSteelSound;

    public AudioManager() {
        backgroundMusicGame = Gdx.audio.newMusic(Gdx.files.internal(GameResources.PLAY_SOUND_PATH));
        shoot = Gdx.audio.newSound(Gdx.files.internal(GameResources.SHOOT_SOUND_PATH));
        death = Gdx.audio.newSound(Gdx.files.internal(GameResources.DEATH_SOUND_PATH));
        startSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.START_SOUND_PATH));
        hitSteelSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.HIT_STEEL_SOUND_PATH));

        backgroundMusicGame.setVolume(0.2f);
        backgroundMusicGame.setLooping(true);
    }
}
