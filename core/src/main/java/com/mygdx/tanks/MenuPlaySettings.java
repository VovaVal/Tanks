package com.mygdx.tanks;

public class MenuPlaySettings {

    public static final int MIN_FRIEND_PLAYERS = 2;
    public static final int MAX_FRIEND_PLAYERS = 4;

    private GamePlayMode mode = GamePlayMode.SINGLE;
    private int friendPlayerCount = MIN_FRIEND_PLAYERS;

    public GamePlayMode getMode() {
        return mode;
    }

    public void setMode(GamePlayMode mode) {
        this.mode = mode;
        if (mode == GamePlayMode.WITH_FRIENDS) {
            if (friendPlayerCount < MIN_FRIEND_PLAYERS || friendPlayerCount > MAX_FRIEND_PLAYERS) {
                friendPlayerCount = MIN_FRIEND_PLAYERS;
            }
        }
    }

    public int getFriendPlayerCount() {
        return friendPlayerCount;
    }

    public void setFriendPlayerCount(int count) {
        if (count >= MIN_FRIEND_PLAYERS && count <= MAX_FRIEND_PLAYERS) {
            friendPlayerCount = count;
        }
    }

    public boolean isWithFriends() {
        return mode == GamePlayMode.WITH_FRIENDS;
    }
}
