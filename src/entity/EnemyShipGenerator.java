package entity;

import engine.GameSettings;
import engine.ItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnemyShipGenerator {

    public static List<EnemyShipFormation> shipFormationList;
    private ItemManager itemManager;
    private int maxFormationCount;

    public EnemyShipGenerator(int maxFormationCount) {
        this.maxFormationCount = maxFormationCount;
        shipFormationList = new ArrayList<>(maxFormationCount);
        itemManager = new ItemManager();
    }

    public void GenerateEnemyShip(final GameSettings gameSettings){
        int index = 0;
        while (index <= maxFormationCount) {
            if(index == maxFormationCount) {
                return ;
            }
            if (!isAlive(index)) break;
            index++;
        }
        EnemyShipFormation enemyShip = new EnemyShipFormation(gameSettings);
        itemManager.assignHasItem(enemyShip);
        System.out.println(index);
        try{
            shipFormationList.set(index, enemyShip);
        }catch (IndexOutOfBoundsException e){
            shipFormationList.add(enemyShip);
        }
    }

    public boolean isAlive(int index){
        try{
            return !shipFormationList.get(index).isEmpty();
        }catch(IndexOutOfBoundsException e){
            return false;
        }
    }

    public void update(){
        for (EnemyShipFormation formation:
             shipFormationList) {
            if(formation == null) continue;
            if(formation.isEmpty()) continue;
            formation.update();
        }
    }

    public void shoot(final Set<Bullet> bullets){
        for (EnemyShipFormation formation:
                shipFormationList) {
            if(formation == null) continue;
            if(formation.isEmpty()) continue;
            formation.shoot(bullets);
        }
    }

    public void draw(){
        for (EnemyShipFormation formation:
                shipFormationList) {
            if(formation == null) continue;
            if(formation.isEmpty()) continue;
            formation.draw();
        }
    }
}
