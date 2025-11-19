package main.java.model.tiles.units.players;

import java.util.List;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.utils.BoardController;

public class Rogue extends Player{

    private int currentEnergy;
    private int cost;

    private static final String ABILITY_NAME = "Fan of Knives";
    private static final int MAXIMUM_ENERGY = 100;
    private static final int LEVELUP_ATTACK = 3;
    private static final int TICK_ENERGY = 10;
    private static final int ABILITY_RANGE = 2;

    public Rogue(String name, int hitPoints, int attack, int defense, int cost) {
        super(name, hitPoints, attack, defense);
        this.currentEnergy = MAXIMUM_ENERGY;
        this.cost = cost;
    }

    @Override
    public void levelUp(){
        super.levelUp();
        int totalHealthGained = healthGain();
        int totalAttackGained = attackGain() + (LEVELUP_ATTACK * this.playerLevel);
        int totalDefenseGained = defenseGain();

        health.increaseMax(totalHealthGained);
        health.heal();
        attack += totalAttackGained;
        defense += totalDefenseGained;
        this.currentEnergy = MAXIMUM_ENERGY;

        messageCallback.send(String.format("%s reached level %d: +%d Health, +%d Attack, +%d Defense", this.name, this.playerLevel, totalHealthGained, totalAttackGained, totalDefenseGained));
        messageCallback.send("\tEnergy has been fully restored.");
    }

    @Override
    public void gameTick() 
    {
        this.currentEnergy = Math.min(MAXIMUM_ENERGY, this.currentEnergy + TICK_ENERGY);
    }
    
    @Override
    public void castSpecialAbility() {
        if (this.currentEnergy < cost) {
            messageCallback.send(String.format("%s tried to use %s, but needs %d energy and only has %d.", name, ABILITY_NAME, cost, currentEnergy));
            return;
        }
        
        messageCallback.send(name + " used Fan of Knives!");
        this.currentEnergy -= cost;
        
        List<Enemy> enemiesInRange = BoardController.getEnemiesInRange(this.position, ABILITY_RANGE);
        if (enemiesInRange.isEmpty()) 
        {
            messageCallback.send("But no enemies were in range.");
            return;
        }

        messageCallback.send("Hitting all enemies in range...");
        for (Enemy e : enemiesInRange)
            e.takeDamage(this.attack, this);
    }

    @Override
    public String describe() {
        return super.describe() + String.format("\tEnergy: %d/%d", currentEnergy, MAXIMUM_ENERGY);
    }

    public int getCurrentEnergy() {
        return this.currentEnergy;
    }

    public void setCurrentEnergy(int i) {
        this.currentEnergy = i;
    }
}
