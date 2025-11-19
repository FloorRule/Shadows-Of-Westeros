

import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.players.Mage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.BoardController;
import main.java.utils.Position;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.FixedGenerator;
import main.java.model.game.Board;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MageTest {

    private Mage mage;
    private Enemy enemy1;
    private Enemy enemy2;
    private Board board;
    private final MessageCallback MOCK_MSG_CALLBACK = (msg) -> {};

    @BeforeEach
    void setUp() {
        // A mage
        mage = new Mage("TestMage", 100, 5, 1, 100, 25, 20, 3, 5);
        mage.initialize(new Position(0, 0), new FixedGenerator(), null, MOCK_MSG_CALLBACK);

        // Create some enemies
        enemy1 = new Monster('s', "LogicExam", 50, 10, 2, 20, 4);
        enemy1.initialize(new Position(1, 0), new FixedGenerator(), MOCK_MSG_CALLBACK);
        enemy2 = new Monster('k', "Info2Exam", 80, 15, 5, 40, 4);
        enemy2.initialize(new Position(0, 1), new FixedGenerator(), MOCK_MSG_CALLBACK);

        // Set up the board
        List<Enemy> enemies = new ArrayList<>(List.of(enemy1, enemy2));
        board = new Board(new ArrayList<>(List.of(mage, enemy1, enemy2)), mage, enemies, 2);
        BoardController.setBoard(board);
    }

    @Test
    void testInitialMana() {
        // Initial mana is manaPool / 4 = 100 / 4 = 25
        assertEquals(25, mage.getCurrentMana(), "Initial mana should be a quarter of the mana pool");
    }

    @Test
    void testGameTickRegensMana() {
        mage.gameTick();
        // At level 1, regenerates 1 mana. 25 + 1 = 26
        assertEquals(26, mage.getCurrentMana(), "Should regenerate 1 mana per tick at level 1");
    }

    @Test
    void testLevelUpIncreasesManaAndSpellPower() {
        mage.addExperience(50); // Level up to 2

        // Mana Pool: 100 + (25 * 2) = 150
        assertEquals(150, mage.getManaPool(), "Mana pool should increase on level up");
        // Spell Power: 20 + (10 * 2) = 40
        assertEquals(40, mage.getSpellPower(), "Spell power should increase on level up");

        // Current Mana: min(current + pool/4, pool) = min(25 + 150/4, 150) = min(25+37, 150) = 62
        assertEquals(62, mage.getCurrentMana(), "Current mana should increase on level up");
    }

    @Test
    void testCastSpecialAbilityFailsWithNotEnoughMana() {
        mage.setCurrentMana(24); // One less than the cost of 25
        int enemy1InitialHealth = enemy1.getHealth().getCurrent();

        mage.castSpecialAbility();

        assertEquals(24, mage.getCurrentMana(), "Mana should not be consumed if cast fails");
        assertEquals(enemy1InitialHealth, enemy1.getHealth().getCurrent(), "Enemy health should not change if cast fails");
    }

    @Test
    void testCastSpecialAbilityConsumesManaAndHitsEnemies() {
        mage.setCurrentMana(50);
        int enemy1Health = enemy1.getHealth().getCurrent(); // 50
        int enemy2Health = enemy2.getHealth().getCurrent(); // 80

        mage.castSpecialAbility();

        // mana consumption
        assertEquals(25, mage.getCurrentMana(), "Casting ability should consume mana");

        boolean damageWasDealt = enemy1.getHealth().getCurrent() < enemy1Health || enemy2.getHealth().getCurrent() < enemy2Health;
        assertTrue(damageWasDealt, "At least one enemy should have taken damage");

        // 60 total damage
        int totalDamageDealt = (enemy1Health - enemy1.getHealth().getCurrent()) + (enemy2Health - enemy2.getHealth().getCurrent());
        assertEquals(60, totalDamageDealt, "Total damage should equal hits * spellPower");
    }
}
