package org.game.snake;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int x, y;
        System.out.println();
        System.out.println("Welcome to 2D Game game");
        System.out.println("Enter the box width and height");
        try (var scanner = new Scanner(System.in)) {
            x = scanner.nextInt();
            y = scanner.nextInt();
            System.out.println("Select your game type");
            System.out.println("# Press 1 for without walls (snake can go beyond wall)");
            System.out.println("# Press 2 for with walls (snake can't go beyond wall)");
            int choice = scanner.nextInt();
            var snake2D = new Game(x, y, choice);
            snake2D.start_game();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
