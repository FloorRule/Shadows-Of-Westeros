package main.java.model.tiles.units.enemies;

import main.java.model.tiles.units.players.Player;
import main.java.utils.BoardController;
import main.java.utils.Direction;
import main.java.utils.Position;

public class Monster extends Enemy{
    private final int visionRange;

    public Monster(char tile, String name, int hitPoints, int attack, int defense, int experienceValue, int visionRange) {
        super(tile, name, hitPoints, attack, defense, experienceValue);
        this.visionRange = visionRange;
        
    }

    @Override
    public void onTurn(Player player) 
    { 
        if (this.getPosition().range(player.getPosition()) < visionRange) {
            // Chase
            int dx = player.getPosition().getX() - position.getX();
            int dy = player.getPosition().getY() - position.getY();
            
            Position nextPos;
            if (Math.abs(dx) > Math.abs(dy))
                nextPos = position.translate(Integer.signum(dx), 0);
            else
                nextPos = position.translate(0, Integer.signum(dy));

            interact(BoardController.getBoard().getTile(nextPos));

        } else {
            // Wonder
            Direction move = Direction.getRandomDirection();
            Position nextPos = position.translate(move.getDeltaX(), move.getDeltaY());
            interact(BoardController.getBoard().getTile(nextPos));
        }
    }


    @Override
    public String describe(){
        return String.format("%-15s Health: %d  Attack: %d  Defense: %d  Experience: %d  Vision: %d", name, health.getCurrent(), attack, defense, experienceValue, visionRange);
    }
}
