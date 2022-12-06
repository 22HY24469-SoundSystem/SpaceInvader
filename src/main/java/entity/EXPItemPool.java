package entity;

import java.util.HashSet;
import java.util.Set;

public class EXPItemPool {
    private static Set<EXPItem> pool = new HashSet<EXPItem>();

    private  EXPItemPool(){

    }

    public static EXPItem getEXP(final int positionX, final int positionY, final int speed){
        EXPItem expItem;
        if(!pool.isEmpty()){
            expItem = pool.iterator().next();
            pool.remove(expItem);
            expItem.setPositionX(positionX - expItem.getWidth() / 2);
            expItem.setPositionY(positionY);
            expItem.setSpeed(speed);
            expItem.setSprite();
        } else {
            expItem = new EXPItem(positionX, positionY,speed);
            expItem.setPositionX(positionX - expItem.getWidth() / 2);
        }
        return expItem;
    }

    public static void recycle(final Set<EXPItem> expItem) {
        pool.addAll(expItem);
    }

}
