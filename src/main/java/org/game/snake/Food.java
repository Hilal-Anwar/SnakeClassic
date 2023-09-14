package org.game.snake;

public record Food(String name, String emoji,int food_points,int extra_food_point) {
    public Food(String name, String emoji, int food_points) {
        this(name, emoji, food_points, 0);
    }
}