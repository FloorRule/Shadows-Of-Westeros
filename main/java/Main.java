package main.java;

import main.java.model.game.Game;
import main.java.view.CLI;


public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar SW.jar <path_to_levels_directory>");
            return;
        }
        
        CLI view = new CLI();
        Game game = new Game(args[0], view.getCallback());
        game.startGame();
    }
}