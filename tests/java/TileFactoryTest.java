

import main.java.control.initializers.TileFactory;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.enemies.Monster;
import main.java.model.tiles.units.enemies.Trap;
import main.java.model.tiles.units.players.Player;
import main.java.model.tiles.units.players.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileFactoryTest {

    private TileFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TileFactory();
    }

    @Test
    void testProducePlayer_JonSnow() {
        Player player = factory.producePlayer(1);
        assertTrue(player instanceof Warrior, "Player 1 should be a Warrior");
        assertEquals("Jon Snow", player.getName());
        assertEquals(300, player.getHealth().getCapacity());
        assertEquals(30, player.getAttack());
    }

    @Test
    void testProducePlayer_InvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.producePlayer(0); // Index is 1-based
        }, "Should throw for index less than 1");

        assertThrows(IllegalArgumentException.class, () -> {
            factory.producePlayer(99); // Index out of bounds
        }, "Should throw for index greater than number of players");
    }

    @Test
    void testProduceEnemy_LannisterSoldier() {
        // Use null for callbacks
        Enemy enemy = factory.produceEnemy('s', null, null, null);
        assertTrue(enemy instanceof Monster, "Enemy 's' should be a Monster");
        assertEquals("Lannister Solider", enemy.getName());
        assertEquals(25, enemy.getExperienceValue());
    }

    @Test
    void testProduceEnemy_QueensTrap() {
        Enemy enemy = factory.produceEnemy('Q', null, null, null);
        assertTrue(enemy instanceof Trap, "Enemy 'Q' should be a Trap");
        assertEquals("Queen's Trap", enemy.getName());
        assertEquals(250, enemy.getHealth().getCapacity());
    }

    @Test
    void testProduceEnemy_InvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.produceEnemy('x', null, null, null);
        }, "Should throw for an undefined enemy character");
    }
}
