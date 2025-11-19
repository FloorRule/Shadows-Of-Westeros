

import main.java.model.game.Board;
import main.java.model.tiles.Empty;
import main.java.model.tiles.Tile;
import main.java.model.tiles.units.players.Player;
import main.java.model.tiles.units.players.Warrior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.utils.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;
    private Player player;
    private Tile emptyTile;

    @BeforeEach
    void setUp() {
        // simple 2x1 board
        player = new Warrior("TestPlayer", 100, 10, 5, 3);
        player.initialize(new Position(0, 0), null, null, null);

        emptyTile = new Empty().initialize(new Position(1, 0));

        List<Tile> tiles = new ArrayList<>();
        tiles.add(player);
        tiles.add(emptyTile);

        board = new Board(tiles, player, new ArrayList<>(), 2);
    }

    @Test
    void testGetTile() {
        assertEquals(player, board.getTile(new Position(0, 0)), "Should return the player at (0,0)");
        assertEquals(emptyTile, board.getTile(new Position(1, 0)), "Should return the empty tile at (1,0)");
        assertNull(board.getTile(new Position(9, 9)), "Should return null for a position not on the board");
    }

    @Test
    void testMoveUnitToSwapsTilesAndPositions() {
        Position oldPlayerPos = player.getPosition();
        Position oldEmptyPos = emptyTile.getPosition();

        // Move the player into the empty tile
        board.moveUnitTo(player, oldEmptyPos);

        // boards map
        assertEquals(player, board.getTile(oldEmptyPos), "Player should now be at the new position on the board");
        assertEquals(emptyTile, board.getTile(oldPlayerPos), "Empty tile should be at the player's old position");

        // internal position of the Tile
        assertEquals(oldEmptyPos, player.getPosition(), "Player's internal position should be updated");
        assertEquals(oldPlayerPos, emptyTile.getPosition(), "Empty tile's internal position should be updated");
    }
}