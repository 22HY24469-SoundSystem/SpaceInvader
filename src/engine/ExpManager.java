package engine;

public class ExpManager {
    private int exp = 0;
    private int lev = 0;
    private int percentExp = 0;

    private int addMaxExp = 200;
    private double maxExp = 600.0;
    public ExpManager(){

    }
    public int getExp(){
        return this.exp;
    }
    public int getMaxExp(){
        return (int)(this.maxExp);
    }
    public int getLev(){
        return lev;
    }
    public int getPercentExp(){
        percentExp = (int)((exp/maxExp)*100);
        if(percentExp >= 100){
            exp -= maxExp;
            percentExp = (int)((exp/maxExp)*100);
            plusLev(1);
            nextLevExp(lev);
        }
        return percentExp;
    }
    public void plusExp(int exp){
        this.exp += exp;
    }

    public void nextLevExp(int level){
        maxExp += level*addMaxExp;
    }
    public void plusLev(int lev) {this.lev += lev;}
}
