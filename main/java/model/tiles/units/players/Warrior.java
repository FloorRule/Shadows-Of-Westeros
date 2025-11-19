package main.java.model.tiles.units.players;

import java.util.List;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.utils.BoardController;

public class Warrior extends Player{

    private final int abilityCooldown;
    private int remainingCooldown;

    private final int ABILITY_RANGE = 3;
    private final int LEVELUP_HEALTH = 5;
    private final int LEVELUP_ATTACK = 2;
    private final int LEVELUP_DEFENSE = 1;
    private final int ABILITY_DEFENSE = 10;
    private final double ABILITY_HIT = 0.1;

    private final String ABILITY_NAME = "Avenger’s Shield";

    public Warrior(String name, int hitPoints, int attack, int defense, int cooldown) {
        super(name, hitPoints, attack, defense);
        this.abilityCooldown = cooldown ;
        this.remainingCooldown = 0;
    }

    @Override
    public void levelUp() {
        super.levelUp();
        int totalHealthGained = healthGain() + (LEVELUP_HEALTH * this.playerLevel);
        int totalAttackGained = attackGain() + (LEVELUP_ATTACK * this.playerLevel);
        int totalDefenseGained = defenseGain() + (LEVELUP_DEFENSE * this.playerLevel);

        health.increaseMax(totalHealthGained);
        health.heal();
        attack += totalAttackGained;
        defense += totalDefenseGained;
        this.remainingCooldown = 0;

        messageCallback.send(String.format("%s reached level %d: +%d Health, +%d Attack, +%d Defense", this.name, this.playerLevel, totalHealthGained, totalAttackGained, totalDefenseGained));
    }

    @Override
    public void gameTick() {
        remainingCooldown = Math.max(0, remainingCooldown - 1);
    }

    @Override
    public void castSpecialAbility() {
        if (remainingCooldown > 0) {
            messageCallback.send(String.format("%s tried to use %s, but it's on cooldown for %d more turns.", name, ABILITY_NAME, remainingCooldown));
            return;
        }
        int hpRestore = defense * 10;
        
        List<Enemy> inRange = BoardController.getEnemiesInRange(this.position, ABILITY_RANGE);
        if (!inRange.isEmpty()) 
        {
            messageCallback.send(String.format("%s used %s!, healing for %d.", name, ABILITY_NAME, hpRestore));
            remainingCooldown = abilityCooldown;
            health.heal(hpRestore);

            Enemy target = inRange.get(generator.generate(inRange.size())); // random
            int damage = (int)(health.getCapacity() * ABILITY_HIT);
            messageCallback.send(name + " strikes " + target.getName() + "!");
            target.takeDamage(damage, this); 
        } else
        {
            messageCallback.send(String.format("%s attempted to use %s!", name, ABILITY_NAME));
            messageCallback.send("But there were no enemies in range.");
        }


    }

    @Override
    public String describe() {
        return super.describe() + String.format("\tCooldown: %d/%d", remainingCooldown, abilityCooldown);
    }

    public int getAbilityCooldown(){
        return this.abilityCooldown;
    }

    public int getRemainingCooldown(){
        return this.remainingCooldown;
    }

    public void setRemainingCooldown(int ticks) {
        this.remainingCooldown = ticks;
    }

}
