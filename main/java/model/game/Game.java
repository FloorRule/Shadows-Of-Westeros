package main.java.model.game;

import main.java.control.initializers.LevelInitializer;
import main.java.control.initializers.TileFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import main.java.model.tiles.Tile;
import main.java.model.tiles.units.enemies.Enemy;
import main.java.model.tiles.units.players.Player;
import main.java.utils.BoardController;
import main.java.utils.Position;
import main.java.utils.callbacks.MessageCallback;
import main.java.utils.generators.RandomGenerator;

public class Game {
    private boolean gameIsOver = false;
    private int currentLevelIndex = 0;
    private final List<Path> levelPaths;
    private Player player;
    private final TileFactory tileFactory;
    private final MessageCallback messageCallback;
    private final LevelInitializer levelLoader;

    public Game(String levelsDir, MessageCallback msg) {
        this.messageCallback = msg;
        this.levelPaths = getFilesFromPath(levelsDir);
        this.tileFactory = new TileFactory();
        this.levelLoader = new LevelInitializer(new RandomGenerator(), msg, () -> gameIsOver = true);
    }

    public void startGame() {
        if (!selectPlayer()) {
            messageCallback.send("Game aborted.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        loadLevel();

        while (true) {
            messageCallback.send(BoardController.getBoard().toString());
            messageCallback.send(player.describe());
            messageCallback.send("Enter action (w/a/s/d for move, e for ability, q to wait): ");

            String input = scanner.nextLine();
            if (input.isEmpty())
                continue;
            char action = input.toLowerCase().charAt(0);

            // Player turn
            if (!gameIsOver)
                handlePlayerAction(action);

            // Enemie turn
            if (!gameIsOver) {
                List<Enemy> currentEnemies = new ArrayList<>(BoardController.getBoard().getEnemies());
                for (Enemy e : currentEnemies)
                    if (e.alive())
                        e.onTurn(player);
                player.gameTick();
            }

            if (gameIsOver) {
                messageCallback.send(BoardController.getBoard().toString());
                messageCallback.send("You have been defeated, GAME OVER!");
                break;
            }
            if (BoardController.getBoard().getEnemies().isEmpty()){
                advanceToNextLevel();
                if (gameIsOver)
                    break;
            }

        }
        scanner.close();
    }
    
    private void handlePlayerAction(char action) {
        int dx = 0, dy = 0;
        switch (action) {
            case 'w' -> dy = -1;
            case 's' -> dy = 1;
            case 'a' -> dx = -1;
            case 'd' -> dx = 1;
            case 'e' -> player.castSpecialAbility();
            case 'q' -> messageCallback.send(player.getName() + " waits.");
            default -> messageCallback.send("Invalid action.");
        }

        if (dx != 0 || dy != 0) {
            Position targetPos = player.getPosition().translate(dx, dy);
            Tile targetTile = BoardController.getBoard().getTile(targetPos);
            player.interact(targetTile);
        }
    }

    private boolean selectPlayer() {
        messageCallback.send("Select a player:");
        List<Player> players = tileFactory.listPlayers();

        for (int i = 0; i < players.size(); i++)
            messageCallback.send(String.format("%d. %s", i + 1, players.get(i).describe()));

        Scanner scanner = new Scanner(System.in);
        try 
        {
            int choice = Integer.parseInt(scanner.nextLine());
            this.player = tileFactory.producePlayer(choice);
            messageCallback.send("You have selected:");
            messageCallback.send(player.getName());

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void loadLevel() {
        Board board = levelLoader.loadLevel(levelPaths.get(currentLevelIndex), player);
        BoardController.setBoard(board);
        messageCallback.send("Starting level " + (currentLevelIndex + 1));
    }

    private void advanceToNextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex >= levelPaths.size()) {
            messageCallback.send("Congratulations! You have defeated all enemies and completed the Dungeon!");
            gameIsOver = true;
        } else {
            messageCallback.send("Level complete! Advancing to the next level...");
            loadLevel();
        }
    }
    // Kinda hard but works
    public static List<Path> getFilesFromPath(String directoryPath) {
        Path dirPath = Paths.get(directoryPath);

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath))
            throw new IllegalArgumentException("The provided path is not a valid directory.");

        Pattern pattern = Pattern.compile("level\\d+\\.txt");

        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> pattern.matcher(path.getFileName().toString()).matches())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading directory: " + directoryPath, e);
        }
    }

}
