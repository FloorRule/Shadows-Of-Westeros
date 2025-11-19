
import main.java.model.game.Board;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.players.Hunter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.BoardController;
import main.java.utils.Position;
import main.java.utils.generators.FixedGenerator;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HunterTest {

    private Hunter hunter;
    private Enemy closeEnemy;
    private Enemy farEnemy;

    @BeforeEach
    void setUp() {
        hunter = new Hunter("TestHunter", 220, 30, 2, 6);
        hunter.initialize(new Position(0, 0), new FixedGenerator(), null, msg -> {});

        // Set up enemies
        closeEnemy = new Monster('s', "LogicExam", 100, 10, 10, 20, 3);
        closeEnemy.initialize(new Position(3, 0), new FixedGenerator(), msg -> {}); // range = 3

        farEnemy = new Monster('k', "Info2Exam", 150, 15, 15, 40, 3);
        farEnemy.initialize(new Position(5, 0), new FixedGenerator(), msg -> {}); // range = 5

        List<Enemy> enemies = new ArrayList<>(List.of(closeEnemy, farEnemy));
        Board board = new Board(new ArrayList<>(List.of(hunter, closeEnemy, farEnemy)), hunter, enemies, 6);
        BoardController.setBoard(board);
    }

    @Test
    void testInitialArrows() {
        // Arrows = 10 * level = 10 * 1 = 10
        assertEquals(10, hunter.getArrowsCount());
    }

    @Test
    void testGameTickAddsArrowOnTenthTick() {
        for (int i = 0; i < 9; i++) {
            hunter.gameTick();
            assertEquals(10, hunter.getArrowsCount(), "Arrows should not increase before the 10th tick");
        }
        hunter.gameTick();
        hunter.gameTick();// 10th tick
        // Arrows = 10 (base) + 1 (level) = 11
        assertEquals(11, hunter.getArrowsCount(), "Should gain arrows equal to level on the 10th tick");
        assertEquals(0, hunter.getTickCount(), "Tick count should reset after gaining arrows");
    }

    @Test
    void testShootTargetsClosestEnemy() {
        int farEnemyInitialHealth = farEnemy.getHealth().getCurrent(); // 150

        hunter.castSpecialAbility();

        // hit the close enemy
        assertTrue(closeEnemy.getHealth().getCurrent() < 100, "Closest enemy should take damage");
        assertEquals(farEnemyInitialHealth, farEnemy.getHealth().getCurrent(), "Far enemy should not take damage");
    }

    @Test
    void testShootConsumesArrow() {
        int initialArrows = hunter.getArrowsCount();
        hunter.castSpecialAbility();
        assertEquals(initialArrows - 1, hunter.getArrowsCount(), "Shooting should consume one arrow");
    }

    @Test
    void testShootFailsWithNoArrows() {
        hunter.setArrowsCount(0);
        int closeEnemyHealth = closeEnemy.getHealth().getCurrent();

        hunter.castSpecialAbility();

        assertEquals(0, hunter.getArrowsCount(), "Arrow count should remain 0");
        assertEquals(closeEnemyHealth, closeEnemy.getHealth().getCurrent(), "Enemy should not take damage if hunter has no arrows");
    }
}
