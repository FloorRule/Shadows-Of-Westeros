package main.java.utils;

import java.util.Random;

public enum Direction {
    RIGHT(1, 0),
    LEFT(-1, 0),
    DOWN(0, 1),
    UP(0, -1);

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }
    private static final Random RAND = new Random();
    private static final Direction[] VALUES = values();
    private static final int SIZE = VALUES.length;

    public static Direction getRandomDirection() {
       return VALUES[RAND.nextInt(SIZE)];
    }
}