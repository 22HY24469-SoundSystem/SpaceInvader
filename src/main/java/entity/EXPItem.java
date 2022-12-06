package entity;

import engine.DrawManager;
import sound.SoundPlay;
import sound.SoundType;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class EXPItem extends Entity{
    private int speed;


    public EXPItem(final int positionX, final int positionY, final int speed) {
        super(positionX, positionY, 3*2, 5*2, Color.WHITE);
        this.setPositionX(positionX -this.getWidth()/2);
        this.speed = speed;
        setSprite();

    }

    public void setSprite () {
        this.spriteType = DrawManager.SpriteType.ItemDrop;
    }

    public void setSpeed(final int speed) {this.speed = speed;}

    public int getSpeed() {return this.speed;}

    public void update () {this.positionY += this.speed;}
}
