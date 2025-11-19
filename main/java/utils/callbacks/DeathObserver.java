package main.java.utils.callbacks;

import main.java.model.tiles.units.enemies.Enemy;

public interface DeathObserver {
    void onEnemyDeath(Enemy enemy);
}