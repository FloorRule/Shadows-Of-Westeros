package main.java.model.tiles.units.players;

import java.util.List;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.utils.BoardController;

public class Mage extends Player{

    private int manaPool;
    private int currentMana;
    private int manaCost;
    private int spellPower;
    private int hitsCount;
    private int abilityRange;

    private static final int LEVELUP_MANAPOOL = 25;
    private static final int LEVELUP_SPELLPOWER = 10;

    private final String ABILITY_NAME = "Blizzard";

    public Mage(String name, int hitPoints, int attack, int defense, int manaPool, int manaCost, int spellPower, int hitCount, int abilityRange) {
        super(name, hitPoints, attack, defense);
        this.manaPool = manaPool;
        this.currentMana = manaPool/4;
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitCount;
        this.abilityRange = abilityRange;
    }

    @Override
    public void levelUp(){
        super.levelUp();

        int totalHealthGained = healthGain();
        int totalAttackGained = attackGain();
        int totalDefenseGained = defenseGain();
        int manaGained = LEVELUP_MANAPOOL * this.playerLevel;
        int spellPowerGained = LEVELUP_SPELLPOWER * this.playerLevel;

        health.increaseMax(totalHealthGained);
        health.heal();
        attack += totalAttackGained;
        defense += totalDefenseGained;
        this.manaPool += manaGained;
        this.currentMana = Math.min(this.currentMana + this.manaPool / 4, this.manaPool);
        this.spellPower += spellPowerGained;

        messageCallback.send(String.format("%s reached level %d: +%d Health, +%d Attack, +%d Defense", this.name, this.playerLevel, totalHealthGained, totalAttackGained, totalDefenseGained));
        messageCallback.send(String.format("\t+%d Maximum Mana, +%d Spell Power", manaGained, spellPowerGained));
    }


    @Override
    public void gameTick() 
    { 
        this.currentMana = Math.min(this.manaPool, (this.currentMana + 1) * this.playerLevel);
    }

    @Override
    public void castSpecialAbility() {
        if (currentMana < manaCost) 
        {
            messageCallback.send(String.format("%s tried to cast %s, but needs %d mana and only has %d.", name, ABILITY_NAME, manaCost, currentMana));
            return;
        }

        messageCallback.send(name + " used "+ABILITY_NAME+"!");
        this.currentMana -= this.manaCost;
        int hits = 0;

        List<Enemy> enemiesInRange = BoardController.getEnemiesInRange(this.position, this.abilityRange);
        while (hits < this.hitsCount && !enemiesInRange.isEmpty()) 
        {
            Enemy randomEnemy = enemiesInRange.get(generator.generate(enemiesInRange.size()));
            randomEnemy.takeSpellDamage(spellPower, this); 
            hits++;

            enemiesInRange = BoardController.getEnemiesInRange(this.position, this.abilityRange); 
        }
        if (enemiesInRange.isEmpty() && hits == 0)
            messageCallback.send("But no enemies were in range.");

    }
    
    @Override
    public String describe() {
        return super.describe() + String.format("\tMana: %d/%d\tSpell Power: %d", currentMana, manaPool, spellPower);
    }

    public int getManaPool() {
        return this.manaPool;
    }

    public int getCurrentMana() {
        return this.currentMana;
    }


    public void setCurrentMana(int i) {
        this.currentMana = i;
    }

    public int getSpellPower() {
        return this.spellPower;
    }
}

