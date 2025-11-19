package main.java.model.tiles;

import main.java.model.tiles.units.Unit;

public interface Visitable {
    public void accept(Unit v);
}
