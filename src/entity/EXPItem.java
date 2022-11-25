package entity;

import engine.DrawManager;

import java.awt.*;

public class EXPItem extends Entity{
    private int speed;

    private boolean isAcquired = false;

    public EXPItem(final int positionX, final int positionY, final int speed) {
        super(positionX, positionY, 3*2, 5*2, Color.WHITE);
        this.setPositionX(positionX -this.getWidth()/2);
        this.speed = speed;
        setSprite();
    }

    public void setSprite () {
        if (!this.isAcquired)
            this.spriteType = DrawManager.SpriteType.ItemDrop;
        else
            this.spriteType = DrawManager.SpriteType.ItemGet;
    }

    public void setSpeed(final int speed) {this.speed = speed;}

    public int getSpeed() {return this.speed;}

    public void update () {this.positionY += this.speed;}

    public void setAcquired(boolean bool){
        this.isAcquired = bool;
    }


    public boolean getAcquired(){
        return this.isAcquired;
    }
}
