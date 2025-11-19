package main.java.model.tiles.units.enemies;

import main.java.model.tiles.units.HeroicUnit;
import main.java.model.tiles.units.players.Player;
import main.java.utils.BoardController;
import main.java.utils.Direction;
import main.java.utils.Position;

public class Boss extends Enemy implements HeroicUnit {
    private final int visionRange;
    private final int abilityFrequency;
    private int combatTicks;

    private static final int COMBAT_TICK = 1;

    private final String ABILITY_NAME = "Shoebodybop";

    public Boss(char tile, String name, int hitPoints, int attack, int defense, int experienceValue, int visionRange, int abilityFrequency) {
        super(tile, name, hitPoints, attack, defense, experienceValue);
        this.visionRange = visionRange;
        this.abilityFrequency = abilityFrequency;
        this.combatTicks = 0;
    }

    @Override
    public void castSpecialAbility() {
        messageCallback.send(name + " used "+ABILITY_NAME+"!");
        Player player = BoardController.getPlayerIfInRange(this.position, this.visionRange);
        if(player != null)
            player.takeDamage(attack, this);
        else
            messageCallback.send(player.getName() + " escaped the Special Attack at the last moment!");

    }

    @Override
    public void onTurn(Player player)
    {
        if (this.getPosition().range(player.getPosition()) < visionRange) {
            if(this.combatTicks == this.abilityFrequency)
            {
                this.combatTicks = 0;
                castSpecialAbility();
            }else
                this.combatTicks += COMBAT_TICK;

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
            this.combatTicks = 0;
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
