package main.java.model.tiles.units;

import main.java.model.tiles.Empty;
import main.java.model.tiles.Tile;
import main.java.model.tiles.Wall;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.players.Player;
import main.java.utils.BoardController;
import main.java.utils.Health;
import main.java.utils.Position;
import main.java.utils.callbacks.DeathCallback;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.Generator;

public abstract class Unit extends Tile implements Visitor{
    protected String name;
    protected Health health;
    protected int attack;
    protected int defense;

    protected Generator generator;
    protected DeathCallback deathCallback;
    protected MessageCallback messageCallback;


    public Unit(char tile, String name, int hitPoints, int attack, int defense) {
        super(tile);
        this.name = name;
        this.health = new Health(hitPoints);
        this.attack = attack;
        this.defense = defense;
    }

    public Unit initialize(Position p, Generator generator, MessageCallback messageCallback) {
        super.initialize(p);
        this.generator = generator;
        this.messageCallback = messageCallback;
        return this;
    }

    public int attack() {
        return generator.generate(attack);
    }

    public int defend() {
        return generator.generate(defense);
    }

    public boolean alive() {
        return health.getCurrent() > 0;
    }

    public String getName() {
        return name;
    }
    
    // damage method
    public void takeDamage(int damage, Unit attacker) {
        int defenseRoll = defend();
        int damageTaken = Math.max(0, damage - defenseRoll);
        health.takeDamage(damageTaken);
        messageCallback.send(String.format("%s rolled %d defense points.", this.name, defenseRoll));
        messageCallback.send(String.format("%s defended against %s's attack, taking %d damage and has %s health remaining.", this.name, attacker.name, damageTaken, this.health.toString()));
                                            
        if (!alive())
            onDeath();
    }
    
    // Spell damage though defense.
    public void takeSpellDamage(int spellDamage, Unit caster) {
        health.takeDamage(spellDamage);
        messageCallback.send(String.format("%s is hit by %s's spell for %d damage and has %s health remaining.", this.name, caster.name, spellDamage, this.health.toString()));
        if (!alive())
            onDeath();
    }

    public void battle(Unit enemy) {
        messageCallback.send(this.name + " engages in combat with " + enemy.name + ".");
        messageCallback.send(this.describe());
        messageCallback.send(enemy.describe());
        
        int attackRoll = this.attack();
        messageCallback.send(this.name + " rolled " + attackRoll + " attack points.");
        enemy.takeDamage(attackRoll, this); // new takeDamge dont delete
        messageCallback.send("");
    }

    public void interact(Tile t) {
        t.accept(this);
    }

    public void visit(Empty e) {
        BoardController.getBoard().moveUnitTo(this, e.getPosition());
    }

    public void visit(Wall w) {
        // Do nothing - movement blocked
    }
    
    public abstract void onTurn(Player player);

    public abstract void visit(Player p);
    public abstract void visit(Enemy e);
    public abstract String describe();

    public abstract void onDeath();

    public Health getHealth() {
        return this.health;
    }
}