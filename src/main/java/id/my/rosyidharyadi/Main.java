package id.my.rosyidharyadi;

import id.my.rosyidharyadi.Model.CPU;
import id.my.rosyidharyadi.Model.Display;
import static id.my.rosyidharyadi.Constant.*;

import javax.swing.JFrame;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Display display = new Display(DISPLAY_ROW_NUM, DISPLAY_COL_NUM);

        JFrame frame = new JFrame("CHIP-8 EMU");
        frame.add(display);
        frame.setSize(800, 440);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CPU cpu = new CPU();
//        cpu.loadROM("roms/1-chip8-logo.ch8");
        cpu.loadROM("roms/IBM Logo.ch8");
        long delay = 60L;
        while (true) {
            TimeUnit time = TimeUnit.MILLISECONDS;
//            byte[][] fakeData = generateRandomDisplayBuffer();
//            display.setDataArray(fakeData);
            cpu.run();
            display.setDataArray(cpu.getGraphicBuffer());
            time.sleep(delay);
        }
    }

    private static byte[][] generateRandomDisplayBuffer()
    {
        byte[][] res = new byte[DISPLAY_ROW_NUM][DISPLAY_COL_NUM];
        Random random = new Random();
        for (int row = 0; row < DISPLAY_COL_NUM; row++) {
            for (int col = 0; col < DISPLAY_ROW_NUM; col++) {
                res[row][col] = (byte) random.nextInt(2);
            }
        }

        return res;
    }
}
