package com.mygdx.tanks.objects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.tanks.GameSettings;

import java.util.ArrayList;

public class TankObject extends GameObject {
    public long lastShotTime;
    private int livesLeft = 3;
    public int angleDeg = 360;
    private boolean enemy;
    private boolean destroyed;
    long startTime;
    long nextMove;
    Vector2 dirMove;
    int coolDown;

    enum EnemyState {
        PATROL,
        CHASE,
        BASE
    }

    private EnemyState state = EnemyState.PATROL;

    private boolean hasShield = false;
    private boolean hasSpeedBoost = false;
    private boolean hasRapidFire = false;
    private boolean enemiesFrozen = false;

    private long shieldEndTime = 0;
    private long speedBoostEndTime = 0;
    private long rapidFireEndTime = 0;
    private long freezeEndTime = 0;

    private float originalSpeed = GameSettings.TANK_SPEED;
    private float originalShootCooldown;


    public TankObject(int x, int y, int width, int height, String texturePath, World world,
                      Boolean enemy, int livesLeft) {
        super(texturePath, x, y, width, height, GameSettings.TANK_BIT, world, "tank");

        lastShotTime = TimeUtils.millis() - GameSettings.SHOOTING_COOL_DOWN;

        body.setLinearDamping(20);
        this.enemy = enemy;
        dirMove = new Vector2();
        startTime = TimeUtils.millis();
        nextMove = startTime + GameSettings.NEXT_MOVE;
        destroyed = false;
        chooseRandomDirection();

        if (enemy){
            coolDown = GameSettings.SHOOTING_COOL_DOWN_ENEMIES;
            originalShootCooldown = GameSettings.SHOOTING_COOL_DOWN_ENEMIES;
        } else {
            coolDown = GameSettings.SHOOTING_COOL_DOWN;
            originalShootCooldown = GameSettings.SHOOTING_COOL_DOWN;
        }

        this.livesLeft = livesLeft;
    }

    private boolean canSeePlayer(TankObject player) {
        if (player == null) return false;

        float dx = Math.abs(player.getX() - getX());
        float dy = Math.abs(player.getY() - getY());

        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        if (distance > GameSettings.VISION_RADIUS) return false;

        return dx < GameSettings.AXIS_EPSILON || dy < GameSettings.AXIS_EPSILON;
    }

    private boolean canSeeEagle(ArrayList<WallsObject> walls) {
        for (WallsObject wall: walls) {
            if (wall != null && wall.getType() == GameSettings.TILE_EAGLE) {

                float dx = Math.abs(wall.getX() - getX());
                float dy = Math.abs(wall.getY() - getY());

                float distance = (float)Math.sqrt(dx * dx + dy * dy);
                if (distance > GameSettings.VISION_RADIUS) return false;

                return dx < GameSettings.AXIS_EPSILON || dy < GameSettings.AXIS_EPSILON;
            }
        } return false;
    }

    private void chasePlayer(TankObject player) {
        int px = player.getX();
        int py = player.getY();

        int x = getX();
        int y = getY();

        if (Math.abs(px - x) < GameSettings.AXIS_EPSILON) {
            // Двигаемся по Y
            dirMove.set(0, Math.signum(py - y));
            angleDeg = (py > y) ? 0 : 180;
        }
        else if (Math.abs(py - y) < GameSettings.AXIS_EPSILON) {
            // Двигаемся по X
            dirMove.set(Math.signum(px - x), 0);
            angleDeg = (px > x) ? 270 : 90;
        }

        body.setLinearVelocity(
            dirMove.x * GameSettings.ENEMY_TANK_SPEED,
            dirMove.y * GameSettings.ENEMY_TANK_SPEED
        );
    }

    private void chaseBase(ArrayList<WallsObject> walls) {
        for (WallsObject wall: walls) {
            if (wall != null && wall.getType() == GameSettings.TILE_EAGLE) {

                int px = wall.getX();
                int py = wall.getY();

                int x = getX();
                int y = getY();

                if (Math.abs(px - x) < GameSettings.AXIS_EPSILON) {
                    // Двигаемся по Y
                    dirMove.set(0, Math.signum(py - y));
                    angleDeg = (py > y) ? 0 : 180;
                }
                else if (Math.abs(py - y) < GameSettings.AXIS_EPSILON) {
                    // Двигаемся по X
                    dirMove.set(Math.signum(px - x), 0);
                    angleDeg = (px > x) ? 270 : 90;
                }

                body.setLinearVelocity(
                    dirMove.x * GameSettings.ENEMY_TANK_SPEED,
                    dirMove.y * GameSettings.ENEMY_TANK_SPEED
                );
            }
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
            body.setLinearVelocity(dir.x * getCurrentSpeed(), dir.y * getCurrentSpeed());
        } else {
            stop();
        }
    }

    public void disablePhysics() {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
        body.setLinearVelocity(0, 0);
    }

    public boolean isEnemy() {
        return enemy;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public void enemyMove(TankObject player, ArrayList<WallsObject> walls) {
        long currentTime = TimeUtils.millis();

        if (canSeeEagle(walls)) {
            state = EnemyState.BASE;
        } else {
            if (player == null || player.isDestroyed()) {
                state = EnemyState.PATROL;
            } else if (canSeePlayer(player)) {
                state = EnemyState.CHASE;
            } else {
                state = EnemyState.PATROL;
            }
        }

        if (state == EnemyState.CHASE) {
            chasePlayer(player);
            return;
        } else if (state == EnemyState.BASE) {
            chaseBase(walls);
        }

        if (currentTime > nextMove) {
            nextMove = currentTime + GameSettings.NEXT_MOVE;
            chooseRandomDirection();
        }

        body.setLinearVelocity(
            dirMove.x * GameSettings.ENEMY_TANK_SPEED,
            dirMove.y * GameSettings.ENEMY_TANK_SPEED
        );
    }

    public void stop() {
        body.setLinearVelocity(0, 0);
    }

    public boolean canShoot() {
        return TimeUtils.millis() - lastShotTime >= getCurrentCooldown();
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

//        if (hasRapidFire && (TimeUtils.millis() / 200) % 2 == 0) {
//            batch.setColor(1f, 0.8f, 0.3f, 0.5f);
//            batch.setColor(1, 1, 1, 1);
//        }
//
//        if (hasShield) {
//            batch.setColor(0.3f, 0.5f, 1f, 0.6f);
//            batch.setColor(1, 1, 1, 1);
//        }
    }


    @Override
    public void hit() {
        if (hasShield) return;

        livesLeft -= 1;
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }

    public int getLiveLeft() {
        return livesLeft;
    }

    public void applyBonus(BonusObject.BonusType type) {
        long currentTime = TimeUtils.millis();

        switch (type) {
            case SHIELD:
                hasShield = true;
                shieldEndTime = currentTime + (long)(GameSettings.SHIELD_DURATION * 1000);
                System.out.println("Shield!");
                break;

            case SPEED:
                hasSpeedBoost = true;
                speedBoostEndTime = currentTime + (long)(GameSettings.SPEED_BOOST_DURATION * 1000);
                System.out.println("Faster move!");
                break;

            case RAPID_FIRE:
                hasRapidFire = true;
                rapidFireEndTime = currentTime + (long)(GameSettings.RAPID_FIRE_DURATION * 1000);
                System.out.println("Rapid shoot!");
                break;

            case LIFE:
                livesLeft = Math.min(livesLeft + 1, 3);
                System.out.println("+ 1 life!");
                break;

            case FREEZE:
                enemiesFrozen = true;
                freezeEndTime = currentTime + (long)(GameSettings.FREEZE_DURATION * 1000);
                System.out.println("Freeze!");
                break;

            case GRENADE:
                System.out.println("Grenade!");
                break;
        }
    }

    public void updateBonuses() {
        long currentTime = TimeUtils.millis();

        if (hasShield && currentTime > shieldEndTime) {
            hasShield = false;
        }

        if (hasSpeedBoost && currentTime > speedBoostEndTime) {
            hasSpeedBoost = false;
        }

        if (hasRapidFire && currentTime > rapidFireEndTime) {
            hasRapidFire = false;
        }

        if (enemiesFrozen && currentTime > freezeEndTime) {
            enemiesFrozen = false;
        }
    }

    public float getCurrentSpeed() {
        return hasSpeedBoost ?
            originalSpeed * GameSettings.SPEED_BOOST_MULTIPLIER :
            originalSpeed;
    }

    public int getCurrentCooldown() {
        if (hasRapidFire) {
            return (int)(originalShootCooldown * GameSettings.RAPID_FIRE_MULTIPLIER);
        }
        return (int)originalShootCooldown;
    }

    public boolean isEnemyFrozen() {
        return enemiesFrozen;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public boolean hasSpeedBoost() {
        return hasSpeedBoost;
    }

    public boolean hasRapidFire() {
        return hasRapidFire;
    }
}
