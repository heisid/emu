package id.my.rosyidharyadi;

import id.my.rosyidharyadi.Model.CPU;
import id.my.rosyidharyadi.Model.Display;
import id.my.rosyidharyadi.Model.Keyboard;

import static id.my.rosyidharyadi.Constant.*;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Display display = new Display(DISPLAY_ROW_NUM, DISPLAY_COL_NUM);
        JPanel debugInfoPanel = new JPanel();

        JFrame frame = new JFrame("CHIP-8 EMU");
        frame.add(display);
        frame.setSize(800, 440);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setFocusable(true);
        frame.requestFocus();

        Keyboard keyboard = new Keyboard();
        frame.addKeyListener(keyboard);

        CPU cpu = new CPU(keyboard);
//        cpu.loadROM("roms/1-chip8-logo.ch8"); // todo: dynamic
//        cpu.loadROM("roms/IBM Logo.ch8");
//        cpu.loadROM("roms/1-chip8-logo.ch8");
//        cpu.loadROM("roms/4-flags.ch8");
//        cpu.loadROM("roms/6-keypad.ch8");
//        cpu.loadROM("roms/delay_timer_test.ch8");
        cpu.loadROM("roms/UFO");
//        cpu.loadROM("roms/Keypad Test [Hap, 2006].ch8");
        long delay = 10L;
        while (true) {
            TimeUnit time = TimeUnit.MILLISECONDS;
            cpu.run();
            display.setDataArray(cpu.getGraphicBuffer());
            time.sleep(delay);
        }
    }
}
