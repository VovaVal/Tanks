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

    public ContactManager(World world, GameScreen gameScreen) {
        this.world = world;
        this.gameScreen = gameScreen;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                GameObject objA = (GameObject) fixA.getUserData();
                GameObject objB = (GameObject) fixB.getUserData();

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
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                GameObject objA = (GameObject) fixtureA.getUserData();
                GameObject objB = (GameObject) fixtureB.getUserData();


                if ((objA instanceof TankObject && objB instanceof WallsObject) ||
                    (objA instanceof WallsObject && objB instanceof TankObject)) {

                    System.out.println("Friction 0");
                    contact.setFriction(0f);
                    contact.setEnabled(true);
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }

            private boolean isTankWall(Object a, Object b) {
                return (a instanceof TankObject && b instanceof WallsObject)
                    || (b instanceof TankObject && a instanceof WallsObject);
            }
        });
    }
}
