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
    public Sound tankDiedMain;
    public Sound btnClick;
    public Sound bonusPickup;
    public Sound startScreenSound;
    public Music menuMusicSound;

    public AudioManager() {
        backgroundMusicGame = Gdx.audio.newMusic(Gdx.files.internal(GameResources.PLAY_SOUND_PATH));
        shoot = Gdx.audio.newSound(Gdx.files.internal(GameResources.SHOOT_SOUND_PATH));
        death = Gdx.audio.newSound(Gdx.files.internal(GameResources.DEATH_SOUND_PATH));
        startSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.START_SOUND_PATH));
        hitSteelSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.HIT_STEEL_SOUND_PATH));
        tankDiedMain = Gdx.audio.newSound(Gdx.files.internal(GameResources.TANK_DIED_SOUND_PATH));
        btnClick = Gdx.audio.newSound(Gdx.files.internal(GameResources.BTN_CLICK_SOUND_PATH));
        bonusPickup = Gdx.audio.newSound(Gdx.files.internal(GameResources.BONUS_PICKUP_SOUND_PATH));
        startScreenSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.START_SCREEN_SOUND_PATH));
        menuMusicSound = Gdx.audio.newMusic(Gdx.files.internal(GameResources.MENU_MUSIC_SOUND_PATH));

        backgroundMusicGame.setVolume(0.2f);
        backgroundMusicGame.setLooping(true);

        menuMusicSound.setVolume(0.2f);
        menuMusicSound.setLooping(true);
    }
}
