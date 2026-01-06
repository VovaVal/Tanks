package com.mygdx.tanks.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.tanks.GameSettings;

public class BulletObject extends GameObject {
    public boolean wasHit;
    public int angleDeg;
    private boolean enemyBullet;

    public BulletObject(float x, float y, int width, int height, String texturePath, World world, Vector2 direction, TankObject tankObject, Boolean enemyBullet) {
        super(texturePath, x, y, width, height, GameSettings.BULLET_BIT, world, "bullet");

        body.setLinearVelocity(new Vector2(
            direction.x * GameSettings.BULLET_SPEED,
            direction.y * GameSettings.BULLET_SPEED)
        );
        body.setBullet(true);

        this.angleDeg = tankObject.angleDeg - 90;
        this.enemyBullet = enemyBullet;

        wasHit = false;
    }

    public boolean isEnemyBullet() {
        return enemyBullet;
    }

    public boolean hasToBeDestroyed() {
        float x = getX();
        float y = getY();

        return wasHit ||
            x < 0 || x > GameSettings.MAP_WIDTH ||
            y < 0 || y > GameSettings.MAP_HEIGHT;
    }

    public boolean isHit() {
        return wasHit;
    }
    @Override
    public void hit() {
        wasHit = true;
    }

    @Override
    public void draw(SpriteBatch batch) {
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
}
