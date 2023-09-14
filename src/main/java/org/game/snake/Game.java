package org.game.snake;

import org.jline.utils.InfoCmp;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;


public class Game {
    private final int width;
    private final int height;
    private final ProgressBar progressBar=new ProgressBar();
    private SnakeBody[] snake;
    private int progress_bar_frame=0;
    private final Map<Integer, Food> food_list = Map.of(
            1, new Food("Apple", "\uD83C\uDF4E", 5),
            2, new Food("WaterMellon", "\uD83C\uDF49", 10),
            3, new Food("Avocado", "\uD83E\uDD51", 5),
            4, new Food("Chicken dum stick", "\uD83C\uDF57", 20,50),
            5, new Food("Shortcake", "\uD83C\uDF70", 8),
            6, new Food("Egg", "\uD83E\uDD5A", 15,30));
    private KeyBoardInput keyBoardInput;
    private Food current_food_item;
    private int foodX;
    private int foodY;
    private final int choice;
    private SnakeOrientation snakeOrientation = SnakeOrientation.HORIZONTAL;

    private Key dir;
    private int snakeLength;
    private int score;
    private boolean gameStatus = true;
    private StringBuffer _game_frame;

    public Game(int width, int height, int choice) {
        this.width = width;
        this.height = height;
        this.choice = choice;
    }

    void start_game() throws InterruptedException, IOException {
        var display=new Display();
        keyBoardInput=new KeyBoardInput(display);
        init();
        current_food_item=getCurrent_food_item();
        while (keyBoardInput.getKeyBoardKey() != Key.ESC) {
            if(progressBar.isVisible){
                if (progress_bar_frame==2)
                {
                    progress_bar_frame=0;
                    progressBar.reduce_progress_by(1);
                }
                else progress_bar_frame++;
            }
            _render_the_frame();
            gameStatus = (gameStatus) ? move() : game_over_message();
            Thread.sleep(snakeOrientation.equals(SnakeOrientation.HORIZONTAL) ? 100 : 90);
            display.terminal.puts(InfoCmp.Capability.clear_screen);
            _game_frame = new StringBuffer();
        }
        System.exit(-1);
    }

    private void init() {
        snake = new SnakeBody[width * height];
        snake[0] = new SnakeBody(width / 2, height / 2, /*"\033[0;31m" +*/ "\uD83D\uDD34" /*+ "\33[0m"*/);
        snake[1] = new SnakeBody(width / 2, height / 2, /*"\033[0;33m" +*/ "\uD83D\uDFE2" /*+ "\33[0m"*/);
        snake[2] = new SnakeBody(width / 2, height / 2, /*"\033[0;33m" +*/ "\uD83D\uDFE2" /*+ "\33[0m"*/);
        /*foodX = (int) (Math.random() * (width - 3) + 2);
        foodY = (int) (Math.random() * (height - 3) + 2);*/
        newFood();
        dir = Key.RIGHT;
        score = 0;
        snakeLength = 3;
        _game_frame = new StringBuffer();
    }

    private boolean game_over_message() {
        _game_frame.append("\nGame Over\n");
        _game_frame.append("Game Master \n");
        _game_frame.append(score).append("\n");
        _game_frame.append("Enter space bar to continue...... :)\n");
        System.out.println(_game_frame);
        if (keyBoardInput.getKeyBoardKey() == Key.SPACE) {
            gameStatus = true;
            resetGame();
        }
        return gameStatus;
    }

    private void resetGame() {
        init();
    }

    private boolean move() {
        for (int i = 0; i <= score / 1000; i++) {
            movement();
        }
        System.out.println(_game_frame);
        return gameStatus;
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
        for (int i = snakeLength - 1; i >= 1; i = i - 1) {
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
                    play("sound/dead.wav");
                }
            }
            case DOWN -> {
                snakeOrientation = SnakeOrientation.VERTICAL;
                snake[0].y++;
                if (snake[0].y == height & choice == 1)
                    snake[0].y = 1;
                else if (snake[0].y == height && choice == 2) {
                    gameStatus = false;
                    play("sound/dead.wav");
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
                    play("sound/dead.wav");
                }
            }
        }
        if (snake[0].x == foodX && snake[0].y == foodY) {
            snake[snakeLength] = new SnakeBody(0, 0, /*"\033[0;33m" +*/ "\uD83D\uDFE2" /*+ "\33[0m"*/);
            newFood();
            if (progressBar.isVisible)
               score = score + current_food_item.extra_food_point();
            else score+=current_food_item.food_points();

            snakeLength++;
        }
        if (headHitsBody()) {
            gameStatus = false;
            play("sound/dead.wav");
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
        progressBar.exit_progress_bar();
        current_food_item=getCurrent_food_item();
        if (current_food_item.name().equals("Chicken dum stick")||current_food_item.name().equals("Egg")){
            progressBar.get_new_progress_bar();
        }
        play("sound/eat.wav");
    }
    private String message(String startWith, int length, Pos pos, String endWith) {
        return switch (pos) {
            case Left, Right -> startWith + (" ".repeat(length - (startWith.length()) - endWith.length())) + endWith;
            case Center -> startWith + (" ".repeat(length - startWith.length() - endWith.length() / 2)) + endWith;
        };
    }

    private void _render_the_frame() {
        _game_frame.append('\n').append(message("", width, Pos.Center, "Snake Classics")).append('\n');
        for (int i = 0; i <= height; i++) {
            for (int j = 0; j <= width; j++) {
                if (i == 0 || i == height || j == 0 || j == width || (j == foodX && i == foodY)) {
                    String food = /*"\33[33;1m" +*/ current_food_item.emoji() /*+ "\33[0m"*/;
                    String wall = "\33[36;1m" + "██" + "\33[0m";
                    if (j == foodX && i == foodY)
                        _game_frame.append(food);
                    else _game_frame.append(wall);
                } else if (isSnakePart(i, j) >= 0) {
                    _game_frame.append(snake[isSnakePart(i, j)].color);
                } else _game_frame.append("  ");
            }
            _game_frame.append("\n");
        }
        _game_frame.append('\n');
        if (progressBar.isVisible){
        _game_frame.append(message("Score : " +score+addSpace(width+1-("Score : " +score).length()-7)+progressBar.getProgress_bar(),
                width * 2 + 2+11, Pos.Right, "Snake Length : " + snakeLength));
        }
        else {
            _game_frame.append(message("Score : " +score,
                    width * 2 + 2, Pos.Right, "Snake Length : " + snakeLength));
        }
    }

    private String addSpace(int i) {
        return " ".repeat(i);
    }

    private int isSnakePart(int i, int j) {
        for (int k = 0; k < snakeLength; k++) {
            SnakeBody snakeBody = snake[k];
            if (j == snakeBody.x && i == snakeBody.y)
                return k;
        }
        return -1;
    }
    Food getCurrent_food_item(){
        return food_list.get((int)(Math.random()*6+1));
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
