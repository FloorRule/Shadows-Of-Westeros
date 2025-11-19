package main.java.utils;

public class Position implements Comparable<Position>{
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double range(Position other){
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position translate(int dx, int dy)
    {
        return new Position(this.x + dx, this.y + dy);
    }
    @Override
    public int compareTo(Position other) {
        if (this.y != other.y)
            return Integer.compare(this.y, other.y);
        return Integer.compare(this.x, other.x);
    }

}
