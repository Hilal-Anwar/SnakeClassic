package org.game.snake;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;

public class Game {
    private final int width;
    private final int height;
    private final ArrayList<SnakeBody> snake = new ArrayList<>();
    private final KeyBoardInput keyBoardInput = new KeyBoardInput();
    private int foodX;
    private int foodY;
    private Key dir = Key.RIGHT;
    private int snakeLength = 3;
    private int score;
    private boolean gameStatus = true;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void start() throws InterruptedException, IOException {
        snake.add(new SnakeBody(width / 2, height / 2));
        snake.add(new SnakeBody(width / 2, height / 2));
        snake.add(new SnakeBody(width / 2, height / 2));
        foodX = (int) (Math.random() * (width - 3) + 2);
        foodY = (int) (Math.random() * (height - 3) + 2);
        while(true){
            if(gameStatus){
                move(score);
                System.out.println();
                System.out.println(message("",width/2,Pos.Center,"Snake Classics"));
                System.out.println();
                draw();
                System.out.println(message("Score : "+score,width+1,Pos.Right,"Snake Length : "+snakeLength));
            }
            else{
                System.out.println("Game Over");
                System.out.println("Game Master");
                System.out.println(score);
                System.out.println("Enter space bar to continue......");
                if (keyBoardInput.getKeyBoardKey() == Key.SPACE) {
                    gameStatus = true;
                    resetGame();
                }
            }
            Thread.sleep(200);
            cls();
        }
    }

    private void resetGame() {
        snake.clear();
        snake.add(new SnakeBody(width / 2, width / 2));
        snake.add(new SnakeBody(width / 2, width / 2));
        snake.add(new SnakeBody(width / 2, width / 2));
        dir = Key.RIGHT;
        score = 0;
        snakeLength = 3;
    }

    private void move(int score) {
        for (int i = 0; i <=score/100 ; i++) {
            movement();
        }
    }

    public void movement(){
        if(keyBoardInput.getKeyBoardKey()==Key.UP && dir !=Key.DOWN)
            dir=Key.UP;
        if(keyBoardInput.getKeyBoardKey()==Key.DOWN && dir !=Key.UP)
            dir=Key.DOWN;
        if(keyBoardInput.getKeyBoardKey()==Key.LEFT && dir !=Key.LEFT)
            dir=Key.LEFT;
        if(keyBoardInput.getKeyBoardKey()==Key.RIGHT && dir !=Key.LEFT)
            dir=Key.RIGHT;
        IntStream.iterate(snake.size()-1,i->i>=1,i->i-1).forEachOrdered(i->{
            snake.get(i).x=snake.get(i-1).x;
            snake.get(i).y=snake.get(i-1).y;
        });
        switch (dir){
            case UP -> {
                snake.get(0).y--;
                if(snake.get(0).y==0)
                    snake.get(0).y=height;
            }
            case DOWN -> {
                snake.get(0).y++;
                if(snake.get(0).y==height)
                    snake.get(0).y=0;
            }
            case LEFT -> {
                snake.get(0).x--;
                if(snake.get(0).x==0)
                    snake.get(0).x=width;
            }
            case RIGHT -> {
                snake.get(0).x++;
                if(snake.get(0).x==width)
                    snake.get(0).x=0;
            }
        }
        if(snake.get(0).x==foodX && snake.get(0).y==foodY){
            snake.add(new SnakeBody(0,0));
            newFood();
            score=score+8;
            snakeLength++;
        }
        if(headHitsBody()){
            gameStatus =false;
            play("sound/die.wav");
        }

    }

    private boolean headHitsBody() {
        return IntStream.range(1,snake.size()).anyMatch(i->snake.get(0).x==snake.get(i).x && snake.get(0).y==snake.get(i).y );
    }

    private void newFood() {
        foodX=(int)(Math.random()*(width-3)+2);
        foodY=(int)(Math.random()*(height-3)+2);
        play("sound/eat.wav");
    }

    private void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
    }

    private String message(String startWith,int length,Pos pos,String endWith){
        return switch (pos){
            case Left,Right ->startWith+(" ".repeat(length-startWith.length()-endWith.length()))+endWith;
            case Center -> startWith+(" ".repeat(length-startWith.length()-endWith.length()/2))+endWith;
        };
    }
    private void draw() {
        var box = new StringBuilder();
        for (int i = 0; i <= height; i++) {
            for (int j = 0; j <= width; j++) {
                if (i == 0 || i == height || j == 0 || j == width || (j == foodX && i == foodY)) {
                    String food = "▓";
                    String wall = "▓";
                    if (j == foodX && i == foodY)
                        box.append(food);
                    else box.append(wall);
                } else if (isSnakePart(i, j)) {
                    String body = "▓";
                    box.append(body);
                } else box.append(" ");
            }
            box.append("\n");
        }
        System.out.println(box);
    }

    private boolean isSnakePart(int i, int j) {
        return snake.stream().anyMatch(snakeBody -> j == snakeBody.x && i == snakeBody.y);
    }
    private void play(String name){
        var url=Game.class.getResource(name);
        Clip audioClip;
        try (var audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(url))){
            var format=audioInputStream.getFormat();
            DataLine.Info info=new DataLine.Info(Clip.class,format);
            audioClip=(Clip) AudioSystem.getLine(info);
            audioClip.open(audioInputStream);
            audioClip.start();;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
