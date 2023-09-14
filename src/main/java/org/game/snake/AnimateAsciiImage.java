package org.game.snake;

public class AnimateAsciiImage {
    private String f1;
    private String f2;
    private String temp;
    private boolean condition;
    public AnimateAsciiImage(String f1, String f2) {
        this.f1 = f1;
        this.f2 = f2;
    }
    public void animate(){
        condition=true;
        Thread animationThread = new Thread(() -> {
            while (condition) {
                temp = f1;
                f1 = f2;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                f2 = temp;
            }
        });
        animationThread.start();
    }
    public void stopAnimation(){
        condition=false;
    }

    public String getF1() {
        return f1;
    }
}
