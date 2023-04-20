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
        cpu.loadROM("roms/1-chip8-logo.ch8"); // todo: dynamic
//        cpu.loadROM("roms/IBM Logo.ch8");
        long delay = 50L;
        while (true) {
            TimeUnit time = TimeUnit.MILLISECONDS;
//            byte[][] fakeData = generateRandomDisplayBuffer();
//            byte[][] fakeData = generatePattern();
//            display.setDataArray(fakeData);
            cpu.run();
            display.setDataArray(cpu.getGraphicBuffer());
            time.sleep(delay);
        }
    }

    /*** Below is for testing **/
    // I know, testing should be on its own. Shut the fuck up.
    private static byte[][] generateRandomDisplayBuffer()
    {
        byte[][] res = new byte[DISPLAY_COL_NUM][DISPLAY_ROW_NUM];
        Random random = new Random();
        for (int i = 0; i < DISPLAY_COL_NUM; i++) {
            for (int j = 0; j < DISPLAY_ROW_NUM; j++) {
                res[i][j] = (byte) random.nextInt(2);
            }
        }

        return res;
    }

    private static byte[][] generatePattern()
    {
        byte[][] patternData = new byte[DISPLAY_ROW_NUM][DISPLAY_COL_NUM];
        for (int a = 0; a < DISPLAY_ROW_NUM; a++) {
            for (int b = 0; b < DISPLAY_COL_NUM; b++) {
                if (a == (b-3) * (b-3))
                    patternData[a][b] = 1;
            }
        }
        return patternData;
    }
}
