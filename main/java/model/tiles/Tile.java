package main.java.model.tiles;

import main.java.model.tiles.units.Unit;
import main.java.utils.Position;

public abstract class Tile implements Visitable{
    protected char tile;
    protected Position position;

    public Tile(char tile){
        this.tile = tile;
    }

    public Tile initialize(Position p){
        this.position = p;
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(tile);
    }

    public abstract void accept(Unit unit);

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
    public char getTile() {
        return tile;
    }
}
