package id.my.rosyidharyadi.Model;

import java.awt.*;
import javax.swing.JPanel;

public class Display extends JPanel {
    private byte[][] dataArray;

    public Display(int numRows, int numCols) {
        this.dataArray = new byte[numRows][numCols];
    }

    public void setDataArray(byte[][] dataArray) {
        this.dataArray = dataArray;
        repaint(); // redraw the panel with the new grid
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int numRows = dataArray.length;
        int numCols = dataArray[0].length;

        // Calculate the size of each square based on the panel size and grid dimensions
        int squareSize = (int) Math.min((double) getWidth() / numCols, (double) getHeight() / numRows);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int x = j * squareSize;
                int y = i * squareSize;

                if (dataArray[i][j] == 0) {
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
        int numRows = dataArray.length;
        int numCols = dataArray[0].length;
        int squareSize = 20; // Default square size
        return new Dimension(numCols * squareSize, numRows * squareSize);
    }
}
