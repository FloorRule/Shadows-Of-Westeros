package main.java.utils;

import main.java.model.game.Board;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.players.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class BoardController {

    private static Board board;

    private BoardController() { } 

    public static void setBoard(Board b) {
        board = b;
    }

    public static Board getBoard() {
        return board;
    }

    public static List<Enemy> getEnemiesInRange(Position center, double maxRange) {
        if (board == null)
            return Collections.emptyList();

        List<Enemy> enemiesInRange = new ArrayList<>();
        for (Enemy e : board.getEnemies())
        {
            if (center.range(e.getPosition()) < maxRange && e.alive())
                enemiesInRange.add(e);
        }
        return enemiesInRange;
    }

    public static Enemy getClosestEnemyInRange(Position center, double maxRange) {
        if (board == null || board.getEnemies() == null || board.getEnemies().isEmpty())
            return null;

        Enemy closestEnemy = null;
        double closestDistance = maxRange;

        for (Enemy e : board.getEnemies())
        {
            if (e.alive())
            {
                double dist = center.range(e.getPosition());
                if (dist <= closestDistance)
                {
                    closestDistance = dist;
                    closestEnemy = e;
                }
            }
        }

        return closestEnemy;
    }

    public static Player getPlayerIfInRange(Position center, double maxRange) {
        if (board == null || board.getTile(center) == null)
            return null;

        Player player = board.getPlayer();
        if (player != null && player.alive()) {
            double distance = center.range(player.getPosition());
            if (distance <= maxRange)
                return player;
        }

        return null;
    }

}
