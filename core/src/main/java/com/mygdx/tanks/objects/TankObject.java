package com.mygdx.tanks.objects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.GameState;

import java.util.Random;

public class TankObject extends GameObject {
    private long lastShotTime;
    private int livesLeft = 3;
    public int angleDeg = 360;
    private boolean enemy;
    private boolean destroyed;
    long startTime;
    long nextMove;
    Vector2 dirMove;
    int coolDown;

    public TankObject(int x, int y, int width, int height, String texturePath, World world,
                      Boolean enemy) {
        super(texturePath, x, y, width, height, GameSettings.TANK_BIT, world, "tank");

        lastShotTime = TimeUtils.millis() - GameSettings.SHOOTING_COOL_DOWN;
        body.setLinearDamping(13);
        this.enemy = enemy;
        dirMove = new Vector2();
        startTime = TimeUtils.millis();
        nextMove = startTime + GameSettings.NEXT_MOVE;
        destroyed = false;
        chooseRandomDirection();
        if (enemy){
            coolDown = GameSettings.SHOOTING_COOL_DOWN_ENEMIES;
        } else {
            coolDown = GameSettings.SHOOTING_COOL_DOWN;
        }
    }

    private void chooseRandomDirection() {
        int dir = (int)(Math.random() * 4);
        switch(dir) {
            case 0: dirMove.set(0, 1); angleDeg = 0; break;
            case 1: dirMove.set(0, -1); angleDeg = 180; break;
            case 2: dirMove.set(-1, 0); angleDeg = 90; break;
            case 3: dirMove.set(1, 0); angleDeg = 270; break;
        }
    }


    public void move(Vector2 dir) {
        System.out.println("x = " + this.getX());
        System.out.println("y = " + this.getY());
        if (dir.len() > 0) {
            dir.nor();
            System.out.println("Move!!!");
            body.setLinearVelocity(dir.x * GameSettings.TANK_SPEED, dir.y * GameSettings.TANK_SPEED);
        } else {
            stop();
        }
    }

    public boolean isEnemy() {
        return enemy;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void enemyMove() {
        long currentTime = TimeUtils.millis();

        if (currentTime > nextMove) {
            nextMove = currentTime + GameSettings.NEXT_MOVE;
            chooseRandomDirection();
        }

        body.setLinearVelocity(dirMove.x * GameSettings.TANK_SPEED, dirMove.y * GameSettings.TANK_SPEED);
    }
    public void stop() {
        body.setLinearVelocity(0, 0);
    }

    public boolean canShoot() {
        return TimeUtils.millis() - lastShotTime >= coolDown;
    }

    public void shoot() {
        lastShotTime = TimeUtils.millis();
    }

    @Override
    public void draw(SpriteBatch batch) {
        System.out.println(angleDeg);

        batch.draw(
            texture,
            getX() - width / 2f,
            getY() - height / 2f,
            width / 2f,
            height / 2f,
            width,
            height,
            1f, 1f,
            angleDeg,
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            false, false
        );
    }


    @Override
    public void hit() {
        livesLeft -= 1;
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }

    public int getLiveLeft() {
        return livesLeft;
    }
}
