package org.game.snake;

public class ProgressBar {
    String progress_bar ="███████████████";
    int value = 0;
    boolean isVisible;

    ProgressBar() {
        isVisible = false;
    }

    String getProgress_bar() {
        if (isVisible && value < 15) {
            var s=progress_bar.substring(0, progress_bar.length() - value);
            if (value==14)
                isVisible=false;
            return "\033[36;1m"+s+"\33[0m";
        } else {
            value = 0;
            return "";
        }
    }

    void reduce_progress_by(int v) {
        if (isVisible)
            value = value + v;
    }
    void exit_progress_bar(){
        isVisible=false;
        value = 0;
    }
    void get_new_progress_bar() {
        isVisible = true;
    }
}
