package com.mygdx.tanks.managers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.tanks.GameSettings;
import com.mygdx.tanks.objects.BulletObject;
import com.mygdx.tanks.objects.GameObject;
import com.mygdx.tanks.objects.TankObject;
import com.mygdx.tanks.objects.WallsObject;
import com.mygdx.tanks.screens.GameScreen;

public class ContactManager {
    World world;
    GameScreen gameScreen;

    AudioManager audioManager;

    public ContactManager(World world, GameScreen gameScreen, AudioManager audioManager) {
        this.world = world;
        this.gameScreen = gameScreen;
        this.audioManager = audioManager;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                Object objA = fixA.getUserData();
                Object objB = fixB.getUserData();

                if ((objA instanceof BulletObject && objB instanceof WallsObject) ||
                    (objA instanceof WallsObject && objB instanceof BulletObject)) {

                    WallsObject wall;
                    BulletObject bullet;

                    if (objA instanceof WallsObject) {
                        wall = (WallsObject) objA;
                        bullet = (BulletObject) objB;
                    } else {
                        wall = (WallsObject) objB;
                        bullet = (BulletObject) objA;
                    }

                    if (bullet.isHit()) return;

                    bullet.hit();

                    if (wall.getType() == GameSettings.TILE_BRICK) {
                        wall.destroy();
                    }
                    else if (wall.getType() == GameSettings.TILE_STEEL && !bullet.isEnemyBullet()) {
                        audioManager.hitSteelSound.play();
                    } else if (wall.getType() == GameSettings.TILE_EAGLE) {
                        System.out.println("Eagle");
                    }
                } else if (objA instanceof BulletObject && objB instanceof BulletObject) {
                    BulletObject bullet1;
                    BulletObject bullet2;

                    bullet1 = (BulletObject) objA;
                    bullet2 = (BulletObject) objB;

                    bullet1.hit();
                    bullet2.hit();
                } else if ((objA instanceof BulletObject && objB instanceof TankObject) ||
                    (objA instanceof TankObject && objB instanceof BulletObject)) {
                    TankObject tank;
                    BulletObject bullet;

                    if (objA instanceof TankObject) {
                        tank = (TankObject) objA;
                        bullet = (BulletObject) objB;
                    } else {
                        tank = (TankObject) objB;
                        bullet = (BulletObject) objA;
                    }

                    bullet.hit();

                    if (bullet.isEnemyBullet() && !tank.isEnemy()) {
                        tank.hit();

                        if (!tank.isAlive()) {
                            audioManager.tankDiedMain.play();
                            tank.setDestroyed(true);
                            gameScreen.tankLives--;
                        }
                    } else if (!bullet.isEnemyBullet() && tank.isEnemy()) {
                        tank.hit();
                    }
                } else if ((objA instanceof BulletObject && "WORLD_BOUND".equals(objB)) ||
                    ("WORLD_BOUND".equals(objA) && objB instanceof BulletObject)) {
                    BulletObject bullet =
                        objA instanceof BulletObject ? (BulletObject) objA : (BulletObject) objB;

                    bullet.hit();
                } else if (objA instanceof TankObject && objB instanceof TankObject) {
                    ((TankObject) objA).stop();
                    ((TankObject) objB).stop();
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }
}
