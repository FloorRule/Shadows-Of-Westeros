package main.java.model.tiles.units.players;

import main.java.model.tiles.units.enemies.Enemy;
import main.java.utils.BoardController;


public class Hunter extends Player{

    private int range;
    private int arrowsCount;
    private int tickCount;

    private static final String ABILITY_NAME = "Shoot";
    private static final int LEVELUP_ATTACK = 2;
    private static final int LEVELUP_DEFENSE = 1;
    private static final int LEVELUP_ARROWS = 10;

    private static final int CAST_COST = 1;

    public Hunter(String name, int hitPoints, int attack, int defense, int range) {
        super(name, hitPoints, attack, defense);
        this.arrowsCount = LEVELUP_ARROWS * playerLevel;
        this.range = range;
        this.tickCount = 0;
    }

    @Override
    public void levelUp(){
        super.levelUp();
        arrowsCount += LEVELUP_ARROWS * playerLevel;
        int attackBonus = LEVELUP_ATTACK * playerLevel;
        int defenseBonus = LEVELUP_DEFENSE * playerLevel;

        attack += attackBonus;
        defense += defenseBonus;
    }

    @Override
    public void gameTick()
    {
        if(this.tickCount == 10)
        {
            this.arrowsCount += playerLevel;
            this.tickCount = 0;
        }else{
            this.tickCount += 1;
        }
    }

    @Override
    public void castSpecialAbility() {
        if (this.arrowsCount <= 0) {
            messageCallback.send(String.format("%s tried to %s, but has no arrows.", name, ABILITY_NAME));
            return;
        }

        Enemy enemieInRange = BoardController.getClosestEnemyInRange(this.position, this.range);
        if (enemieInRange != null)
        {
            messageCallback.send(name + " fired an arrow at "+enemieInRange.getName()+"!");
            this.arrowsCount -= CAST_COST;
            enemieInRange.takeDamage(this.attack, this);
        }else
            messageCallback.send(name + " tried to shoot an arrow but there were no enemies in range.");

    }

    @Override
    public String describe() {
        return super.describe() + String.format("\tArrows: %d\tRange: %d", arrowsCount, range);
    }

    public int getArrowsCount() {
        return this.arrowsCount;
    }

    public int getTickCount() {
        return this.tickCount;
    }

    public void setArrowsCount(int i) {
        this.arrowsCount = i;
    }
}