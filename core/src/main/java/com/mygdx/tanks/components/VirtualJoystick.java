package com.mygdx.tanks.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class VirtualJoystick {
    private Vector2 center;  // центр джойстика (фиксирован)
    private Vector2 touchPoint;  // позиция внутреннего круга

    private float outerRadius;
    private float innerRadius;

    private Texture outerTexture;
    private Texture innerTexture;

    private boolean isPressed = false;
    private int activePointer = -1;

    private ScreenViewport viewport;

    private Vector2 targetPoint;

    private static final float SMOOTH_SPEED = 10f;
    private static final float DEAD_ZONE = 0.15f;

    public VirtualJoystick(ScreenViewport screenViewport,
                           float x, float y, float outerRadius, float innerRadius,
                           String outerTexturePath, String innerTexturePath) {

        this.viewport = screenViewport;

        this.center = new Vector2(x, y);
        this.touchPoint = new Vector2(x, y);

        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;

        this.outerTexture = new Texture(outerTexturePath);
        this.innerTexture = new Texture(innerTexturePath);

        this.targetPoint = new Vector2(x, y);
    }

    public void update() {
        if (activePointer == -1) {
            for (int i = 0; i < 5; i++) {
                if (!Gdx.input.isTouched(i)) continue;

                Vector3 touch = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                viewport.unproject(touch);
                Vector2 touchPos = new Vector2(touch.x, touch.y);

                if (touchPos.dst(center) <= outerRadius) {
                    activePointer = i;
                    isPressed = true;
                    targetPoint.set(touchPos);
                    break;
                }
            }
        }

        if (activePointer != -1) {
            if (!Gdx.input.isTouched(activePointer)) {
                // палец отпущен
                activePointer = -1;
                isPressed = false;
                targetPoint.set(center);
            } else {
                Vector3 touch = new Vector3(Gdx.input.getX(activePointer), Gdx.input.getY(activePointer), 0);
                viewport.unproject(touch);
                Vector2 touchPos = new Vector2(touch.x, touch.y);

                Vector2 dir = new Vector2(touchPos).sub(center);

                if (dir.len() > outerRadius) {
                    dir.nor().scl(outerRadius);
                }

                targetPoint.set(center).add(dir);
                isPressed = true;
            }
        } else {
            // нет активного пальца — возвращаемся в центр
            targetPoint.lerp(center, Gdx.graphics.getDeltaTime() * SMOOTH_SPEED);
            isPressed = false;
        }

        // плавное движение внутреннего круга
        touchPoint.lerp(targetPoint, Gdx.graphics.getDeltaTime() * SMOOTH_SPEED);
    }

    public Vector2 getDirection() {
        Vector2 dir = new Vector2(touchPoint).sub(center);
        float len = dir.len() / outerRadius;

        if (len < DEAD_ZONE) {
            return Vector2.Zero;
        }

        float clampedLen = Math.min(1f, len);
        return dir.nor().scl((clampedLen - DEAD_ZONE) / (1f - DEAD_ZONE));
    }

    public void draw(SpriteBatch batch) {
        batch.draw(outerTexture,
            center.x - outerRadius,
            center.y - outerRadius,
            outerRadius * 2,
            outerRadius * 2);
        batch.draw(innerTexture,
            touchPoint.x - innerRadius,
            touchPoint.y - innerRadius,
            innerRadius * 2,
            innerRadius * 2);
    }

    public void dispose() {
        outerTexture.dispose();
        innerTexture.dispose();
    }
}
