package com.mygdx.tanks.objects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.tanks.GameSettings;

public class TankObject extends GameObject {
    private long lastShotTime;
    private int livesLeft = 3;
    public int angleDeg = 360;

    public TankObject(int x, int y, int width, int height, String texturePath, World world) {
        super(texturePath, x, y, width, height, GameSettings.TANK_BIT, world, "tank");

        lastShotTime = TimeUtils.millis() - GameSettings.SHOOTING_COOL_DOWN;
        body.setLinearDamping(13);
        livesLeft = 3;
        // body.setBullet(true);
    }

    public void move(Vector2 dir) {
        if (dir.len() > 0) {
            dir.nor();
            System.out.println("Move!!!");
            body.setLinearVelocity(dir.x * GameSettings.TANK_SPEED, dir.y * GameSettings.TANK_SPEED);
        } else {
            stop();
        }
    }

    public void stop() {
        body.setLinearVelocity(0, 0);
    }

    public boolean canShoot() {
        return TimeUtils.millis() - lastShotTime >= GameSettings.SHOOTING_COOL_DOWN;
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
