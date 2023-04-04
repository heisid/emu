package id.my.rosyidharyadi.Model;

import java.awt.*;
import javax.swing.JPanel;

public class Display extends JPanel {
    private int[][] grid;

    public Display(int numRows, int numCols) {
        this.grid = new int[numRows][numCols];
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
        repaint(); // redraw the panel with the new grid
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int numRows = grid.length;
        int numCols = grid[0].length;

        // Calculate the size of each square based on the panel size and grid dimensions
        int squareSize = (int) Math.min((double) getWidth() / numCols, (double) getHeight() / numRows);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int x = j * squareSize;
                int y = i * squareSize;

                if (grid[i][j] == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }

                g.fillRect(x, y, squareSize, squareSize);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int numRows = grid.length;
        int numCols = grid[0].length;
        int squareSize = 20; // Default square size
        return new Dimension(numCols * squareSize, numRows * squareSize);
    }
}
