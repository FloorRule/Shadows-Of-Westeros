

import main.java.model.game.Board;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.players.Warrior;
import main.java.utils.BoardController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.Position;
import main.java.utils.generators.FixedGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarriorTest {

    private Warrior warrior;
    private Enemy enemy;

    @BeforeEach
    void setUp() {
        // Create a new warrior
        warrior = new Warrior("Test Warrior", 100, 10, 5, 3);
        warrior.initialize(new Position(0, 0), new FixedGenerator(), null, (msg) -> {});

        // Create an enemy
        enemy = new Monster('s', "Dummy", 50, 10, 5, 20, 5);
        enemy.initialize(new Position(1, 0), new FixedGenerator(), (msg) -> {});
    }

    // Helper method
    private void setupBoard() {
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        Board board = new Board(new ArrayList<>(List.of(warrior, enemy)), warrior, enemies, 2);
        BoardController.setBoard(board);
    }

    @Test
    void testInitialStats() {
        assertEquals("Test Warrior", warrior.getName());
        assertEquals(100, warrior.getHealth().getCapacity());
        assertEquals(10, warrior.getAttack());
        assertEquals(5, warrior.getDefense());
        assertEquals(0, warrior.getRemainingCooldown());
    }

    @Test
    void testGameTickDecrementsCooldown() {
        warrior.setRemainingCooldown(3);
        warrior.gameTick();
        assertEquals(2, warrior.getRemainingCooldown(), "Cooldown should decrease by 1 on game tick");
    }

    @Test
    void testGameTickDoesNotGoBelowZero() {
        warrior.setRemainingCooldown(0);
        warrior.gameTick();
        assertEquals(0, warrior.getRemainingCooldown(), "Cooldown should not go below zero");
    }

    @Test
    void testCastSpecialAbilitySetsCooldown() {
        // requires a board
        setupBoard();

        warrior.castSpecialAbility();

        // ability sets the remaining cooldown to the full ability cooldown
        assertEquals(3, warrior.getRemainingCooldown(), "Casting ability should put it on cooldown");
    }

    @Test
    void testCastSpecialAbilityHeals() {
        // board environment
        setupBoard();

        // Reduce health first
        warrior.takeDamage(70, enemy); // Health should be < 100
        int healthBeforeAbility = warrior.getHealth().getCurrent();

        warrior.castSpecialAbility();

        // Heal amount is defense * 10 = 5 * 10 = 50.
        int expectedHealth = Math.min(100, healthBeforeAbility + 50);
        assertEquals(expectedHealth, warrior.getHealth().getCurrent(), "Special ability should heal for 10 * defense points");
    }

    @Test
    void testCastSpecialAbilityOnCooldownDoesNothing() {
        setupBoard();
        warrior.setRemainingCooldown(1);
        int initialHealth = warrior.getHealth().getCurrent();

        warrior.castSpecialAbility();

        // Cooldown should not be reset
        assertEquals(1, warrior.getRemainingCooldown(), "Should not be able to cast while on cooldown");
        assertEquals(initialHealth, warrior.getHealth().getCurrent(), "Health should not change when cast fails");
    }

    @Test
    void testLevelUpResetsCooldownAndBoostsStats() {
        warrior.setRemainingCooldown(2);
        warrior.addExperience(50); // Level up from 1 to 2

        assertEquals(2, warrior.getPlayerLevel(), "Player should be level 2");
        assertEquals(0, warrior.getExperience(), "Experience should be reset after level up");
        assertEquals(0, warrior.getRemainingCooldown(), "Cooldown should be reset on level up");

        // Check stat gains for leveling up to level 2
        assertEquals(130, warrior.getHealth().getCapacity(), "Health pool should increase on level up");

        assertEquals(22, warrior.getAttack(), "Attack should increase on level up");

        assertEquals(9, warrior.getDefense(), "Defense should increase on level up");
    }
}
