package main.java.utils;

public class Health {
    private int capacity;
    private int current;

    public Health(int capacity) {
        this.capacity = capacity;
        this.current = capacity;
    }

    public int takeDamage(int damageTaken) {
        damageTaken = Math.max(0, damageTaken);
        damageTaken = Math.min(current, damageTaken);
        current -= damageTaken;
        return damageTaken;
    }

    public int getCurrent() {
        return current;
    }

    public int getCapacity() {
        return capacity;
    }
    
    public void setCurrent(int currentHealth) {
        this.current = Math.max(0, Math.min(capacity, currentHealth));
    }

    public void increaseMax(int healthGain) {
        capacity += healthGain;
    }

    // Heals full
    public void heal() {
        current = capacity;
    }

    // Heals specific amount
    public void heal(int amount) {
        this.current = Math.min(this.capacity, this.current + amount);
    }
    
    @Override
    public String toString() {
        return current + "/" + capacity;
    }
}