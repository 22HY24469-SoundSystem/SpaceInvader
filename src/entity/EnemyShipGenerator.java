package entity;

import engine.GameSettings;
import engine.ItemManager;

import java.util.ArrayList;
import java.util.List;

public class EnemyShipGenerator {

    public static List<EnemyShipFormation> shipFormationList;
    private ItemManager itemManager;
    private int maxFormationCount;

    public EnemyShipGenerator(int maxFormationCount) {
        this.maxFormationCount = maxFormationCount;
        shipFormationList = new ArrayList<>(maxFormationCount);
        itemManager = new ItemManager();
    }

    public int GenerateEnemyShip(final GameSettings gameSettings){
        int index = 0;
        while (index <= maxFormationCount) {
            if(index == maxFormationCount) {
                return -1;
            }
            if (!isAlive(index)) break;
            index++;
        }
        EnemyShipFormation enemyShip = new EnemyShipFormation(gameSettings);
        itemManager.assignHasItem(enemyShip);
        if(this.shipFormationList.isEmpty()) shipFormationList.add(enemyShip);
        else shipFormationList.set(index, enemyShip);
        return index;
    }

    private boolean isAlive(int index){
        if(this.shipFormationList.isEmpty()) return false;
        if(!this.shipFormationList.get(index).isEmpty()) return true;
        return false;
    }
}
