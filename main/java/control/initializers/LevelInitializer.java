package main.java.control.initializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import main.java.model.game.Board;
import main.java.model.tiles.Tile;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.players.Player;
import main.java.utils.Position;
import main.java.utils.callbacks.DeathCallback;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.Generator;

public class LevelInitializer {
    private final TileFactory factory = new TileFactory();
    private final Generator rng;
    private final MessageCallback msg;
    private final DeathCallback deathCb;

    public LevelInitializer(Generator rng, MessageCallback msg, DeathCallback deathCb) {
        this.rng = rng;
        this.msg = msg;
        this.deathCb = deathCb;
    }

    public Board loadLevel(Path levelFile, Player player) {
        List<Tile> tiles = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        int width = 0, y = 0;

        try {
            for (String line : Files.readAllLines(levelFile))
            {
                width = Math.max(width, line.length());
                for (int x = 0; x < line.length(); x++)
                {
                    char c = line.charAt(x);
                    Position pos = new Position(x, y);

                    switch (c)
                    {
                        case '.' -> tiles.add(factory.produceEmpty(pos));
                        case '#' -> tiles.add(factory.produceWall(pos));
                        case '@' -> {
                            player.initialize(pos, rng, deathCb, msg);
                            tiles.add(player);
                        }
                        default -> {// enemy
                            Enemy e = factory.produceEnemy(c, pos, rng, msg);
                            e.addObserver(player);
                            enemies.add(e);
                            tiles.add(e);
                        }
                    }
                }
                y++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read " + levelFile, e);
        }
        return new Board(tiles, player, enemies, width);
    }
}
