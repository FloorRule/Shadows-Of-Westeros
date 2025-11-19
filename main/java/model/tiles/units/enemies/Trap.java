package main.java.model.tiles.units.enemies;

import main.java.model.tiles.units.players.Player;

public class Trap extends Enemy{
    private final int visibilityTime;
    private final int invisibilityTime;
    private int ticksCount = 0;
    private boolean visible = true;

    private final char ogTile;

    public Trap(char tile, String name, int hitPoints, int attack, int defense, int experienceValue, int visTime, int invisTime) {
        super(tile, name, hitPoints, attack, defense, experienceValue);
        this.visibilityTime = visTime;
        this.invisibilityTime = invisTime;
        this.ogTile = tile;
    }

    @Override
    public void onTurn(Player player) 
    { 
        visible = ticksCount < visibilityTime;
        
        this.tile = visible ? getOriginalTile() : '.'; 
                                         
        if (ticksCount == (visibilityTime + invisibilityTime))
            ticksCount = 0;
        else 
            ticksCount++;
        
        if (this.position.range(player.getPosition()) < 2) 
            battle(player);
        
    }
    
    private char getOriginalTile() 
    { 
        return ogTile; 
    }

    @Override
    public String describe(){
        return String.format("%-15s Health: %d  Attack: %d  Defense: %d  Experience: %d", name, health.getCurrent(), attack, defense, experienceValue);
    }
}
