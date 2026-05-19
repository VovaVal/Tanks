package com.mygdx.tanks;

import com.mygdx.tanks.components.VirtualJoystick;
import com.mygdx.tanks.objects.TankObject;

/** Локальный игрок в режиме «с друзьями». */
public class LocalPlayerSlot {

    public final int index;
    public TankObject tank;
    public VirtualJoystick joystick;
    public float autoShootTimer;
    public boolean deathProcessed;

    public LocalPlayerSlot(int index) {
        this.index = index;
    }

    public boolean isAlive() {
        return tank != null && !tank.isDestroyed() && tank.isAlive();
    }
}
