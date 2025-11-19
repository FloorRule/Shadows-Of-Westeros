package main.java.model.tiles.units.enemies;

import main.java.model.tiles.units.Unit;
import main.java.model.tiles.units.players.Player;
import main.java.utils.BoardController;
import main.java.utils.callbacks.DeathObserver;
import java.util.ArrayList;
import java.util.List;
public abstract class Enemy extends Unit {
    protected int experienceValue;
    private List<DeathObserver> observers;

    public Enemy(char tile, String name, int hitPoints, int attack, int defense, int experienceValue) {
        super(tile, name, hitPoints, attack, defense);
        this.experienceValue = experienceValue;
        this.observers = new ArrayList<>();
    }

    public int getExperienceValue() {
        return experienceValue;
    }

    @Override
    public void accept(Unit unit) {
        unit.visit(this);
    }

    @Override
    public void visit(Enemy e){
        // Do nothing
    }

    @Override
    public void visit(Player p){
        battle(p);
        if (!p.alive()){
            p.onDeath();
        }
    }

    public void addObserver(DeathObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (DeathObserver observer : observers)
            observer.onEnemyDeath(this);
    }

    @Override
    public void onDeath() {
        messageCallback.send(this.name + " died.");
        BoardController.getBoard().removeEnemy(this);
        notifyObservers(); // Notify - enemy dies
    }

    @Override
    public abstract String describe();

    @Override
    public abstract void onTurn(Player player);
}
