package com.mygdx.tanks.objects;

import static com.mygdx.tanks.GameSettings.SCALE;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.tanks.GameSettings;

public class GameObject {

    public int width;
    public int height;

    public Body body;
    Texture texture;

    public short cBits;

    GameObject(String texturePath, float x, float y, int width, int height, short cBits, World world, String kind) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;

        texture = new Texture(texturePath);
        body = createBody(x, y, world, kind);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, getX() - (width / 2f), getY() - (height / 2f), width, height);
    }

    public int getX() {
        return (int) (body.getPosition().x / SCALE);
    }

    public int getY() {
        return (int) (body.getPosition().y / SCALE);
    }

    public void setX(int x) {
        body.setTransform(x * SCALE, body.getPosition().y, 0);
    }

    public void setY(int y) {
        body.setTransform(body.getPosition().x, y * SCALE, 0);
    }

    public void hit(){

    }

    private Body createBody(float x, float y, World world, String kind) {
        BodyDef bodyDef = new BodyDef();

        if (kind.equals("tank") || kind.equals("bullet")) {
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        } else {
            bodyDef.type = BodyDef.BodyType.StaticBody; // brick, steel, eagle
        }

        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        Shape shape;

        PolygonShape box = new PolygonShape();
        // shape.setAsBox((width / 2f) * SCALE, (height / 2f) * SCALE);
        box.setAsBox((width / 2f) * SCALE, (height / 2f) * SCALE);
        shape = box;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = (bodyDef.type == BodyDef.BodyType.DynamicBody) ? 1.0f : 0.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = cBits;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        body.setTransform(x * SCALE, y * SCALE, 0);
        return body;
    }

}
