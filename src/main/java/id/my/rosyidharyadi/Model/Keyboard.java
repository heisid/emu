package id.my.rosyidharyadi.Model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Keyboard implements KeyListener {
    // this is so wacky
    private int keyBeingPressed = -1; // -1: no relevant key is pressed
    private final HashMap<Integer, Integer> keyboardMap = new HashMap<>(
            Map.ofEntries(
                    entry(KeyEvent.VK_1, 0x1),
                    entry(KeyEvent.VK_2, 0x2),
                    entry(KeyEvent.VK_3, 0x3),
                    entry(KeyEvent.VK_4, 0xC),
                    entry(KeyEvent.VK_Q, 0x4),
                    entry(KeyEvent.VK_W, 0x5),
                    entry(KeyEvent.VK_E, 0x6),
                    entry(KeyEvent.VK_R, 0xD),
                    entry(KeyEvent.VK_A, 0x7),
                    entry(KeyEvent.VK_S, 0x8),
                    entry(KeyEvent.VK_D, 0x9),
                    entry(KeyEvent.VK_F, 0xE),
                    entry(KeyEvent.VK_Z, 0xA),
                    entry(KeyEvent.VK_X, 0x0),
                    entry(KeyEvent.VK_C, 0xB),
                    entry(KeyEvent.VK_V, 0xF)
            )
    );

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        // nothin'
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyboardMap.containsKey(keyCode)) {
            keyBeingPressed = keyboardMap.get(keyCode);
//            System.out.println("Key pressed: " + keyBeingPressed);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
//        System.out.println("Key released ");
        keyBeingPressed = -1;
    }

    public Integer getKeyBeingPressed() {
        return keyBeingPressed;
    }
}
