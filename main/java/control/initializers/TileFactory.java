package main.java.control.initializers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import main.java.model.tiles.Empty;
import main.java.model.tiles.Tile;
import main.java.model.tiles.Wall;
import main.java.model.tiles.units.enemies.Boss;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.enemies.Trap;
import main.java.model.tiles.units.players.*;
import main.java.utils.Position;
import main.java.utils.callbacks.DeathCallback;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.Generator;

public class TileFactory {

    private final List<Supplier<Player>> playerSuppliers;
    private final Map<Character, Supplier<Enemy>> enemySuppliers;

    public TileFactory() {
        this.playerSuppliers = initPlayerSuppliers();
        this.enemySuppliers = initEnemySuppliers();
    }

    private List<Supplier<Player>> initPlayerSuppliers() {
        return Arrays.asList(
                // Warriors
                () -> new Warrior("Jon Snow", 300, 30, 4, 3),
                () -> new Warrior("The Hound", 400, 20, 6, 5),
                // Mages
                () -> new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6),
                () -> new Mage("Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4),
                // Rogues
                () -> new Rogue("Arya Stark", 150, 40, 2, 20),
                () -> new Rogue("Bronn", 250, 35, 3, 50),
                // Hunter here
                () -> new Hunter("Ygritte", 220, 30, 2, 6)
        );
    }

    private Map<Character, Supplier<Enemy>> initEnemySuppliers() {
        return Map.ofEntries(
                // Monsters
                Map.entry('s', () -> new Monster('s', "Lannister Solider", 80, 8, 3, 25, 3)),
                Map.entry('k', () -> new Monster('k', "Lannister Knight", 200, 14, 8, 50, 4)),
                Map.entry('q', () -> new Monster('q', "Queen's Guard", 400, 20, 15, 100, 5)),
                Map.entry('z', () -> new Monster('z', "Wright", 600, 30, 15, 100, 3)),
                Map.entry('b', () -> new Monster('b', "Bear-Wright", 1000, 75, 30, 250, 4)),
                Map.entry('g', () -> new Monster('g', "Giant-Wright", 1500, 100, 40, 500, 5)),
                Map.entry('w', () -> new Monster('w', "White Walker", 2000, 150, 50, 1000, 6)),
                Map.entry('M', () -> new Boss('M', "The Mountain", 1000, 60, 25, 500, 6, 5)),
                Map.entry('C', () -> new Boss('C', "Queen Cersei", 100, 10, 10, 1000, 1, 8)),
                Map.entry('K', () -> new Boss('K', "Night's King", 5000, 300, 150, 5000, 8, 3)),
                // Traps
                Map.entry('B', () -> new Trap('B', "Bonus Trap", 1, 1, 1, 250, 1, 5)),
                Map.entry('Q', () -> new Trap('Q', "Queen's Trap", 250, 50, 10, 100, 3, 7)),
                Map.entry('D', () -> new Trap('D', "Death Trap", 500, 100, 20, 250, 1, 10))
        );
    }
    
    public List<Player> listPlayers() {
        return playerSuppliers.stream().map(Supplier::get).collect(Collectors.toList());
    }

    public Player producePlayer(int playerIndex) {
        if (playerIndex < 1 || playerIndex > playerSuppliers.size())
            throw new IllegalArgumentException("Invalid player index: " + playerIndex);

        return playerSuppliers.get(playerIndex - 1).get();
    }

    public Enemy produceEnemy(char tile, Position p, Generator g, MessageCallback m) {
        Supplier<Enemy> supplier = enemySuppliers.get(tile);
        if (supplier == null)
            throw new IllegalArgumentException("No enemy definition for tile: " + tile);
            
        Enemy e = supplier.get();
        e.initialize(p, g, m);
        return e;
    }

    public Tile produceEmpty(Position p) {
        return new Empty().initialize(p);
    }

    public Tile produceWall(Position p) {
        return new Wall().initialize(p);
    }
}