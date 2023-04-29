package id.my.rosyidharyadi;

import id.my.rosyidharyadi.Model.CPU;
import id.my.rosyidharyadi.Model.Display;
import id.my.rosyidharyadi.Model.Keyboard;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Display display = new Display(Constant.DISPLAY_ROW_NUM, Constant.DISPLAY_COL_NUM);
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

        String romFile = "";
        try {
            romFile = args[0];
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("No ROM file provided. Exiting...");
            System.exit(1);
        }
//        romFile = "roms/1-chip8-logo.ch8";
//        romFile = "roms/IBM Logo.ch8";
//        romFile = "roms/1-chip8-logo.ch8";
//        romFile = "roms/4-flags.ch8";
//        romFile = "roms/6-keypad.ch8";
//        romFile = "roms/delay_timer_test.ch8";
//        romFile = "roms/Life [GV Samways, 1980].ch8";
//        romFile = "roms/Keypad Test [Hap, 2006].ch8";

        cpu.loadROM(romFile);
        long delay = 1000L / 500L;
        while (true) {
            TimeUnit time = TimeUnit.MILLISECONDS;
            cpu.run();
            display.setDataArray(cpu.getGraphicBuffer());
            time.sleep(delay);
        }
    }
}
