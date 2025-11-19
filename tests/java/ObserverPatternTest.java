

import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.players.Player;
import main.java.model.tiles.units.players.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.BoardController;
import main.java.utils.Position;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.FixedGenerator;
import main.java.model.game.Board;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObserverPatternTest {

    private Player player;
    private Enemy enemy;
    private final MessageCallback MOCK_MSG_CALLBACK = (msg) -> {};

    @BeforeEach
    void setUp() {
        player = new Warrior("TestPlayer", 500, 200, 50, 5); // Strong
        player.initialize(new Position(0,0), new FixedGenerator(), null, MOCK_MSG_CALLBACK);

        enemy = new Monster('s', "XP-Dummy", 50, 5, 5, 77, 3); // 77 XP
        enemy.initialize(new Position(1,0), new FixedGenerator(), MOCK_MSG_CALLBACK);

        enemy.addObserver(player);

        // Board setup
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        Board board = new Board(new ArrayList<>(List.of(player, enemy)), player, enemies, 2);
        BoardController.setBoard(board);
    }

    @Test
    void testExperienceGainedOnNormalBattleKill() {
        // Player moves onto enemy tile and kills it in 1 shot
        player.interact(enemy);

        assertTrue(!enemy.alive(), "Enemy should be dead");
        assertEquals(27, player.getExperience(), "Player should gain experience from a battle kill");
    }

    @Test
    void testExperienceGainedOnAbilityKill() {
        enemy.takeDamage(100, player); // kills the enemy

        assertTrue(!enemy.alive(), "Enemy should be dead");
        assertEquals(27, player.getExperience(), "Player should gain experience from an ability kill");
    }

    @Test
    void testNoExperienceGainedIfEnemyDoesNotDie() {
        player = new Warrior("WeakPlayer", 100, 10, 5, 3); // Not strong enough
        player.initialize(new Position(0,0), new FixedGenerator(), null, MOCK_MSG_CALLBACK);
        enemy.addObserver(player);

        enemy.takeDamage(20, player);

        assertTrue(enemy.alive(), "Enemy should still be alive");
        assertEquals(0, player.getExperience(), "Player should not gain experience if the enemy survives");
    }
}
