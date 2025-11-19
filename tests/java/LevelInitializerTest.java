

import main.java.control.initializers.LevelInitializer;
import main.java.model.game.Board;
import main.java.model.tiles.Empty;
import main.java.model.tiles.Wall;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.players.Player;
import main.java.model.tiles.units.players.Warrior;
import main.java.utils.BoardController;
import main.java.utils.generators.FixedGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.Position;
import main.java.utils.callbacks.DeathCallback;
import main.java.utils.callbacks.MessageCallback;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelInitializerTest {

    private LevelInitializer levelInitializer;
    private Player player;
    private Path testLevelPath;

    private final MessageCallback MOCK_MSG_CALLBACK = (msg) -> {};
    private final DeathCallback MOCK_DEATH_CALLBACK = () -> {};

    @BeforeEach
    void setUp() throws URISyntaxException {
        levelInitializer = new LevelInitializer(new FixedGenerator(), MOCK_MSG_CALLBACK, MOCK_DEATH_CALLBACK);
        player = new Warrior("TestPlayer", 100, 10, 5, 3);

        testLevelPath = Paths.get(getClass().getClassLoader().getResource("test_level1.txt").toURI());
    }

    @Test
    void testLoadLevel_CreatesBoardCorrectly() {
        Board board = levelInitializer.loadLevel(testLevelPath, player);

        assertNotNull(board, "Board should not be null after loading a level");
        // testfile is 5 characters wide
        assertEquals(5, board.getWidth(), "Board width should be calculated correctly");
    }

    @Test
    void testLoadLevel_PlacesTilesInCorrectPositions() {
        Board board = levelInitializer.loadLevel(testLevelPath, player);

        // Row 0,Col 0 should be a Wall
        assertTrue(board.getTile(new Position(0, 0)) instanceof Wall, "Position (0,0) should be a Wall");

        // Row 1 Col 1 should be the Player
        assertSame(player, board.getTile(new Position(1, 1)), "Position (1,1) should be the player instance");
        assertEquals(new Position(1,1).getX(), player.getPosition().getX(), "Player's internal position should be set correctly");
        assertEquals(new Position(1,1).getY(), player.getPosition().getY(), "Player's internal position should be set correctly");

        // Row 1 Col 2 should be a Monster ('s')
        assertTrue(board.getTile(new Position(2, 1)) instanceof Monster, "Position (2,1) should be a Monster");
        assertEquals('s', board.getTile(new Position(2, 1)).getTile(), "Tile character should be 's'");

        // Row 1 Col 3 should be an Empty tile
        assertTrue(board.getTile(new Position(3, 1)) instanceof Empty, "Position (3,1) should be an Empty tile");

        // Row 2 Col 3 should be another Monster ('k')
        assertTrue(board.getTile(new Position(3, 2)) instanceof Monster, "Position (3,2) should be a Monster");
        assertEquals('k', board.getTile(new Position(3, 2)).getTile(), "Tile character should be 'k'");
    }

    @Test
    void testLoadLevel_PopulatesEnemyList() {
        Board board = levelInitializer.loadLevel(testLevelPath, player);
        List<Enemy> enemies = board.getEnemies();

        assertNotNull(enemies, "Enemy list should not be null");
        assertEquals(2, enemies.size(), "There should be exactly 2 enemies on the board");
    }

    @Test
    void testLoadLevel_PlayerIsRegisteredAsObserver() {
        class PlayerSpy extends Warrior {
            int observerCount = 0;
            PlayerSpy()
            {
                super("Spy", 1, 1, 1, 1);
                this.initialize(new Position(0,0), new FixedGenerator(), (msg) -> {});
            }

            @Override
            public void onEnemyDeath(Enemy enemy)
            {
                observerCount++;
            }
        }

        PlayerSpy playerSpy = new PlayerSpy();

        Board board = levelInitializer.loadLevel(testLevelPath, playerSpy);

        BoardController.setBoard(board);

        for (Enemy e : new ArrayList<>(board.getEnemies())) {
            e.onDeath();
        }

        // notified for each death
        assertEquals(2, playerSpy.observerCount, "Player should be notified of each enemy's death");
    }
}
