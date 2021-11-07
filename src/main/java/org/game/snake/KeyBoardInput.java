package org.game.snake;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class KeyBoardInput {
     private Key keyBoardKey=Key.RIGHT;

    public KeyBoardInput() {
      new Thread(()->{
          try(Terminal terminal= TerminalBuilder.terminal()){
              while (true){
                  setKeyBoardKey(getKeys(terminal.reader().read()));
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }).start();
    }
    private static Key getKeys(int ch) {
        return switch (ch) {
            case 65 -> Key.UP;
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
