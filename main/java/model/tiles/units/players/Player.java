package main.java.model.tiles.units.players;

import main.java.model.tiles.units.HeroicUnit;
import main.java.model.tiles.units.Unit;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.utils.BoardController;
import main.java.utils.Health;
import main.java.utils.Position;
import main.java.utils.callbacks.DeathCallback;
import main.java.utils.callbacks.DeathObserver;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.Generator;

public abstract class Player extends Unit implements HeroicUnit, DeathObserver{
    public static final char PLAYER_TILE = '@';
    protected static final int LEVEL_REQUIREMENT = 50;
    protected static final int HEALTH_GAIN = 10;
    protected static final int ATTACK_GAIN = 4;
    protected static final int DEFENSE_GAIN = 1;

    protected int playerLevel;
    protected int experience;

    public Player(String name, int hitPoints, int attack, int defense) {
        super(PLAYER_TILE, name, hitPoints, attack, defense);
        this.playerLevel = 1;
        this.experience = 0;
    }

    public Player initialize(Position p, Generator generator, DeathCallback deathCallback, MessageCallback messageCallback)
    {
        super.initialize(p, generator, messageCallback);
        this.deathCallback = deathCallback;
        return this;
    }

    @Override
    public abstract void castSpecialAbility();
    public abstract void gameTick();


    public void addExperience(int experienceValue) {
        this.experience += experienceValue;
        messageCallback.send(name + " gained " + experienceValue + " experience!");
        while (experience >= levelRequirement())
            levelUp();
    }

    public void levelUp() {
        this.experience -= levelRequirement();
        this.playerLevel++;

    }

    @Override
    public void onTurn(Player player) {
        // can be empty
    }

    @Override
    public void accept(Unit unit) {
        unit.visit(this);
    }

    @Override
    public void visit(Player p) {
         // can be empty
    }

    @Override
    public void onEnemyDeath(Enemy enemy) {
        addExperience(enemy.getExperienceValue());
    }
    @Override
    public void visit(Enemy e) {
        battle(e);
        if (!e.alive()) {
            BoardController.getBoard().moveUnitTo(this, e.getPosition());
        }
    }

    @Override
    public void onDeath() {
        this.tile = 'X';
        if (deathCallback != null)
            deathCallback.onDeath();
    }
    
    @Override
    public String describe() {
        return String.format("%s\tHealth: %s\tAttack: %d\tDefense: %d\tLevel: %d\tExperience: %d/%d", name, health.toString(), attack, defense, playerLevel, experience, levelRequirement());
    }

    protected int levelRequirement(){
        return LEVEL_REQUIREMENT * playerLevel;
    }

    protected int healthGain(){
        return HEALTH_GAIN * playerLevel;
    }

    protected int attackGain(){
        return ATTACK_GAIN * playerLevel;
    }

    protected int defenseGain(){
        return DEFENSE_GAIN * playerLevel;
    }

    public Health getHealth(){
        return health;
    }

    public int getAttack(){
        return attack;
    }

    public int getDefense(){
        return defense;
    }

    public int getPlayerLevel(){
        return playerLevel;
    }

    public int getExperience(){
        return experience;
    }


}
