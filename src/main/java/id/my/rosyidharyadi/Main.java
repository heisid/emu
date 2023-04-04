package id.my.rosyidharyadi;

import id.my.rosyidharyadi.Model.Display;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
//        int[][] grid = {
//                {0, 0, 1, 0, 1},
//                {1, 0, 0, 1, 0},
//                {0, 1, 1, 0, 1},
//                {1, 0, 1, 0, 0}
//        };

        int DISPLAY_WIDE = 64;
        int DISPLAY_HEIGHT = 32;
        Display display = new Display(DISPLAY_HEIGHT, DISPLAY_WIDE);

        JFrame frame = new JFrame("Grid Draw");
        frame.add(display);
        frame.setSize(800, 440);
        int[][] newGrid = new int[DISPLAY_HEIGHT][DISPLAY_WIDE];
        for (int i = 0; i < DISPLAY_HEIGHT; i++) {
            for (int j = 0; j < DISPLAY_WIDE; j++) {
                // ngetest doang su
                if (i == j) {
                    newGrid[i][j] = 1;
                }
            }
        }
        display.setGrid(newGrid); // update the grid with the new array


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
