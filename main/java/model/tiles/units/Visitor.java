package main.java.model.tiles.units;

import main.java.model.tiles.Empty;
import main.java.model.tiles.Wall;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.players.Player;

public interface Visitor {
    public void visit(Empty e);
    public void visit(Wall w);
    public void visit(Player p);
    public void visit(Enemy e);
}
