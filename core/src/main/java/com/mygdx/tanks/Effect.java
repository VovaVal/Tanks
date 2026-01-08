package com.mygdx.tanks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Effect {
    private Texture[] frames;
    private float frameTime;
    private int currentFrame;
    private float timer;
    public boolean finished;
    public Vector2 position;
    private float size;

    public Effect(Texture[] frames, Vector2 position, float frameTime, float size) {
        this.frames = frames;
        this.position = position;
        this.frameTime = frameTime;
        this.size = size;
        this.currentFrame = 0;
        this.timer = 0;
        this.finished = false;
    }

    public void update(float delta) {
        if (finished) return;

        timer += delta;
        if (timer >= frameTime) {
            timer = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                finished = true;
            }
        }
    }

    public void draw(Batch batch) {
        if (!finished && currentFrame < frames.length) {
            batch.draw(frames[currentFrame], position.x - size / 2, position.y - size / 2, size, size);
        }
    }
}
