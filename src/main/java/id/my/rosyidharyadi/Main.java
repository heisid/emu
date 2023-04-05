package id.my.rosyidharyadi;

import id.my.rosyidharyadi.Model.Display;
import static id.my.rosyidharyadi.Constant.*;

import javax.swing.JFrame;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        int[][] grid = {
//                {0, 0, 1, 0, 1},
//                {1, 0, 0, 1, 0},
//                {0, 1, 1, 0, 1},
//                {1, 0, 1, 0, 0}
//        };

        Display display = new Display(DISPLAY_ROW_NUM, DISPLAY_COL_NUM);

        JFrame frame = new JFrame("Grid Draw");
        frame.add(display);
        frame.setSize(800, 440);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        long delay = 1L;
        while (true) {
            TimeUnit time = TimeUnit.SECONDS;
            byte[][] fakeData = generateRandomDisplayBuffer();
            display.setDataArray(fakeData);
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
