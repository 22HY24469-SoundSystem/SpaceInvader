package entity;

import engine.DrawManager;

import java.awt.*;

public class Item extends Entity {

    //private static int dropSpeed;
    //private static int duration;
    private int speed;
    public enum ItemType{

        BulletSpeedItem,

        PointUpItem,

        ShieldItem,

        SpeedUpItem,

        ExtraLifeItem,

        MachineGun,

        EnemyShipSpeedItem

    }

    private ItemType itemType;

    private boolean isAcquired;


    public Item(final int positionX, final int positionY, final int speed, final ItemType itemType) {
            super(positionX, positionY, 3 * 2, 5 * 2, Color.ORANGE);
            this.setPositionX(positionX -this.getWidth()/2);
            this.speed = speed;
            this.itemType = itemType;
            this.isAcquired = false;
        }


    public void update () {this.positionY += this.speed;}


    public void setSprite () {
        if (!this.isAcquired)
            this.spriteType = DrawManager.SpriteType.ItemDrop;
        else
            this.spriteType = DrawManager.SpriteType.ItemGet;
    }


    public void setAcquired(boolean bool){
        this.isAcquired = bool;
    }


    public boolean getAcquired(){
        return this.isAcquired;
    }


    public ItemType getItemType(){
        return this.itemType;
    }

    }
