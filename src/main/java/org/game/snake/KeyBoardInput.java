package org.game.snake;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class KeyBoardInput {
     private Key keyBoardKey=Key.RIGHT;
    public KeyBoardInput(Display display) {
        new Thread(() -> {
            while (true) {
                try {
                    setKeyBoardKey(getKeys(display.terminal.reader().read()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private static Key getKeys(int ch) {
        return switch (ch) {
            case 65 -> Key.UP;
            case 27->  Key.ESC;
            case 66 -> Key.DOWN;
            case 68 -> Key.LEFT;
            case 67 -> Key.RIGHT;
            default -> Key.SPACE;
        };
    }

    public Key getKeyBoardKey() {
        return keyBoardKey;
    }

    public void setKeyBoardKey(Key keyBoardKey) {
        this.keyBoardKey = keyBoardKey;
    }
}
