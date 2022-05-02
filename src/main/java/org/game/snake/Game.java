package org.game.snake;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class Game {
    private final int width;
    private final int height;
    private SnakeBody[] snake;
    private final KeyBoardInput keyBoardInput = new KeyBoardInput();
    private int foodX;
    private int foodY;
    private final int choice;
    private SnakeOrientation snakeOrientation = SnakeOrientation.HORIZONTAL;

    private Key dir = Key.RIGHT;
    private int snakeLength = 3;
    private int score;
    private boolean gameStatus = true;

    public Game(int width, int height, int choice) {
        this.width = width;
        this.height = height;
        this.choice = choice;
    }

    void start_game() throws InterruptedException, IOException {
        snake = new SnakeBody[width * height];
        snake[0] = new SnakeBody(width / 2, height / 2);
        snake[1] = new SnakeBody(width / 2, height / 2);
        snake[2] = new SnakeBody(width / 2, height / 2);
        foodX = (int) (Math.random() * (width - 3) + 2);
        foodY = (int) (Math.random() * (height - 3) + 2);
        while (keyBoardInput.getKeyBoardKey() != Key.ESC) {
            if (gameStatus) {
                move(score);
                System.out.println();
                System.out.println(message("", width, Pos.Center, "Snake Classics"));
                System.out.println();
                draw();
                System.out.println(message("Score : " + score, width * 2 + 2, Pos.Right, "Snake Length : " + snakeLength));
            } else {
                System.out.println("Game Over");
                System.out.println("Game Master");
                System.out.println(score);
                System.out.println("Enter space bar to continue......");
                if (keyBoardInput.getKeyBoardKey() == Key.SPACE) {
                    gameStatus = true;
                    resetGame();
                }
            }
            Thread.sleep(snakeOrientation.equals(SnakeOrientation.HORIZONTAL) ? 50 : 80);
            clear_the_screen();
        }
        System.exit(-1);
    }

    private void resetGame() {
        snake = new SnakeBody[width * height];
        snake[0] = new SnakeBody(width / 2, width / 2);
        snake[1] = new SnakeBody(width / 2, width / 2);
        snake[2] = new SnakeBody(width / 2, width / 2);
        dir = Key.RIGHT;
        score = 0;
        snakeLength = 3;
    }

    private void move(int score) {
        for (int i = 0; i <= score / 100; i++) {
            movement();
        }
    }

    public void movement() {
        if (keyBoardInput.getKeyBoardKey() == Key.UP && dir != Key.DOWN)
            dir = Key.UP;
        if (keyBoardInput.getKeyBoardKey() == Key.DOWN && dir != Key.UP)
            dir = Key.DOWN;
        if (keyBoardInput.getKeyBoardKey() == Key.LEFT && dir != Key.RIGHT)
            dir = Key.LEFT;
        if (keyBoardInput.getKeyBoardKey() == Key.RIGHT && dir != Key.LEFT)
            dir = Key.RIGHT;
        for (int i = snakeLength-1; i >= 1; i = i - 1) {
            snake[i].x = snake[i - 1].x;
            snake[i].y = snake[i - 1].y;
        }
        switch (dir) {
            case UP -> {
                snakeOrientation = SnakeOrientation.VERTICAL;
                snake[0].y--;
                if (snake[0].y == 0 & choice == 1)
                    snake[0].y = height - 1;
                else if (snake[0].y == 0 && choice == 2) {
                    gameStatus = false;
                    play("sound/die.wav");
                }
            }
            case DOWN -> {
                snakeOrientation = SnakeOrientation.VERTICAL;
                snake[0].y++;
                if (snake[0].y == height & choice == 1)
                    snake[0].y = 1;
                else if (snake[0].y == height && choice == 2) {
                    gameStatus = false;
                    play("sound/die.wav");
                }
            }
            case LEFT -> {
                snakeOrientation = SnakeOrientation.HORIZONTAL;
                snake[0].x--;
                if (snake[0].x == 0 & choice == 1)
                    snake[0].x = width - 1;
                else if (snake[0].x == 0 && choice == 2) {
                    gameStatus = false;
                    play("sound/die.wav");
                }
            }
            case RIGHT -> {
                snakeOrientation = SnakeOrientation.HORIZONTAL;
                snake[0].x++;
                if (snake[0].x == width & choice == 1)
                    snake[0].x = 1;
                else if (snake[0].x == width && choice == 2) {
                    gameStatus = false;
                    play("sound/die.wav");
                }
            }
        }
        if (snake[0].x == foodX && snake[0].y == foodY) {
            snake[snakeLength ] = new SnakeBody(0, 0);
            newFood();
            score = score + 8;
            snakeLength++;
        }
        if (headHitsBody()) {
            gameStatus = false;
            play("sound/die.wav");
        }

    }

    private boolean headHitsBody() {
        int bound = snakeLength;
        for (int i = 1; i < bound; i++) {
            if (snake[0].x == snake[i].x && snake[0].y == snake[i].y) {
                return true;
            }
        }
        return false;
    }

    private void newFood() {
        foodX = (int) (Math.random() * (width - 3) + 2);
        foodY = (int) (Math.random() * (height - 3) + 2);
        play("sound/eat.wav");
    }

    private void clear_the_screen() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    private String message(String startWith, int length, Pos pos, String endWith) {
        return switch (pos) {
            case Left, Right -> startWith + (" ".repeat(length - startWith.length() - endWith.length())) + endWith;
            case Center -> startWith + (" ".repeat(length - startWith.length() - endWith.length() / 2)) + endWith;
        };
    }

    private void draw() {
        var box = new StringBuilder();
        for (int i = 0; i <= height; i++) {
            for (int j = 0; j <= width; j++) {
                if (i == 0 || i == height || j == 0 || j == width || (j == foodX && i == foodY)) {
                    String food = "\33[31;1m" + "██" + "\33[0m";
                    String wall = "\33[36;1m" + "██" + "\33[0m";
                    if (j == foodX && i == foodY)
                        box.append(food);
                    else box.append(wall);
                } else if (isSnakePart(i, j)) {
                    String body = "\33[33;1m" + "██" + "\33[0m";
                    box.append(body);
                } else box.append("  ");
            }
            box.append("\n");
        }
        System.out.println(box);
    }

    private boolean isSnakePart(int i, int j) {
        for (int k = 0; k < snakeLength; k++) {
            SnakeBody snakeBody = snake[k];
            if (j == snakeBody.x && i == snakeBody.y)
                return true;
        }
        return false;
    }

    private void play(String name) {
        var url = Game.class.getResource(name);
        Clip audioClip;
        try (var audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(url))) {
            var format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioInputStream);
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    enum SnakeOrientation {
        VERTICAL, HORIZONTAL
    }
}
