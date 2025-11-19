package main.java.model.game;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import main.java.model.tiles.Empty;
import main.java.model.tiles.Tile;
import main.java.model.tiles.units.Unit;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.players.Player;
import main.java.utils.Position;

public class Board {
    private Map<Position, Tile> board;
    private Player player;
    private List<Enemy> enemies;
    private final int width;

    public Board(List<Tile> tiles, Player p, List<Enemy> enemies, int width){
        this.player = p;
        this.enemies = enemies;
        this.width = width;
        this.board = new TreeMap<>();
        for(Tile t : tiles){
            board.put(t.getPosition(), t);
        }
    }

    public Tile getTile(Position pos){ return board.get(pos);}
    public Tile setTile(Position pos, Tile tile){ return board.put(pos, tile);}
    public List<Enemy> getEnemies(){ return enemies; }

    public void removeEnemy(Enemy enemy) { 
        enemies.remove(enemy);
        board.put(enemy.getPosition(), new Empty());
    }

    public void moveUnitTo(Unit mover, Position newPos) {
        Tile tileAtNewPos = board.get(newPos);
        Position oldPos = mover.getPosition();

        board.put(newPos, mover);
        board.put(oldPos, tileAtNewPos);

        mover.setPosition(newPos);
        tileAtNewPos.setPosition(oldPos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Position, Tile> entry : board.entrySet()){
            sb.append(entry.getValue().toString());
            if(entry.getKey().getX() == width-1){
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public int getWidth() {
        return this.width;
    }
}
