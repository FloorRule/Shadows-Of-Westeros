

import main.java.model.game.Board;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.players.Rogue;
import main.java.utils.generators.FixedGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.BoardController;
import main.java.utils.Position;
import main.java.utils.callbacks.MessageCallback;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RogueTest {

    private Rogue rogue;
    private Enemy inRangeEnemy;
    private Enemy outOfRangeEnemy;
    private Board board;

    private final MessageCallback MOCK_MSG_CALLBACK = (msg) -> {};

    @BeforeEach
    void setUp() {
        // A rogue
        rogue = new Rogue("TestRogue", 150, 40, 2, 50);
        rogue.initialize(new Position(0, 0), new FixedGenerator(), null, MOCK_MSG_CALLBACK);

        // within Fan of Knives range (< 2)
        inRangeEnemy = new Monster('s', "Close-Dummy", 100, 10, 5, 20, 3);
        inRangeEnemy.initialize(new Position(1, 1), new FixedGenerator(), MOCK_MSG_CALLBACK);

        // outside of Fan of Knives range
        outOfRangeEnemy = new Monster('k', "Far-Dummy", 100, 10, 5, 20, 3);
        outOfRangeEnemy.initialize(new Position(2, 2), new FixedGenerator(), MOCK_MSG_CALLBACK);

        // Set up the board
        List<Enemy> enemies = new ArrayList<>(List.of(inRangeEnemy, outOfRangeEnemy));
        board = new Board(new ArrayList<>(List.of(rogue, inRangeEnemy, outOfRangeEnemy)), rogue, enemies, 3);
        BoardController.setBoard(board);
    }

    @Test
    void testInitialEnergyIsMax() {
        assertEquals(100, rogue.getCurrentEnergy(), "Rogue should start with maximum energy");
    }

    @Test
    void testGameTickRegensEnergy() {
        rogue.setCurrentEnergy(50);
        rogue.gameTick();
        // TICK_ENERGY constant is 10
        assertEquals(60, rogue.getCurrentEnergy(), "Should regenerate 10 energy per game tick");
    }

    @Test
    void testGameTickDoesNotExceedMaxEnergy() {
        rogue.setCurrentEnergy(95);
        rogue.gameTick();
        assertEquals(100, rogue.getCurrentEnergy(), "Energy regeneration should not exceed the maximum of 100");
    }

    @Test
    void testLevelUpResetsEnergyAndBoostsAttack() {
        rogue.setCurrentEnergy(20);
        rogue.addExperience(50); // Level up to 2

        assertEquals(100, rogue.getCurrentEnergy(), "Energy should be reset to maximum on level up");
        assertEquals(2, rogue.getPlayerLevel(), "Player should be level 2");

        // (4 * 2) + (3 * 2) = 6. Total = 54
        assertEquals(54, rogue.getAttack(), "Attack should increase on level up");
    }

    @Test
    void testCastAbilityFailsWithInsufficientEnergy() {
        rogue.setCurrentEnergy(49); // Cost is 50
        int initialEnergy = rogue.getCurrentEnergy();
        int inRangeEnemyHealth = inRangeEnemy.getHealth().getCurrent();

        rogue.castSpecialAbility();

        assertEquals(initialEnergy, rogue.getCurrentEnergy(), "Energy should not be consumed if cast fails");
        assertEquals(inRangeEnemyHealth, inRangeEnemy.getHealth().getCurrent(), "Enemy health should not change if cast fails");
    }

    @Test
    void testCastAbilityConsumesEnergyAndHitsOnlyInRangeEnemies() {
        int initialEnergy = rogue.getCurrentEnergy();
        int inRangeEnemyHealth = inRangeEnemy.getHealth().getCurrent();
        int outOfRangeEnemyHealth = outOfRangeEnemy.getHealth().getCurrent();

        rogue.castSpecialAbility();

        // energy was consumed
        assertEquals(initialEnergy - 50, rogue.getCurrentEnergy(), "Correct amount of energy should be consumed");

        // enemy took damage
        assertEquals(inRangeEnemyHealth - 38, inRangeEnemy.getHealth().getCurrent(), "Enemy in range should take damage");

        // out-of-range enemy took NO damage
        assertEquals(outOfRangeEnemyHealth, outOfRangeEnemy.getHealth().getCurrent(), "Enemy out of range should not take damage");
    }
}
