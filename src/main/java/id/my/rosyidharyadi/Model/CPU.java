package id.my.rosyidharyadi.Model;

import static id.my.rosyidharyadi.Constant.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static id.my.rosyidharyadi.Model.Utility.*;

public class CPU {
    private byte[] memory = new byte[MEMORY_SIZE];
    private byte[] vRegister = new byte[V_REGISTER_SIZE];
    private short indexRegister;
    private short programCounter;

    private short[] stack = new short[STACK_SIZE];
    private byte stackPointer;

    private byte delayTimer;
    private byte soundTimer;
    private byte[] font = new byte[FONT_LENGTH * FONT_HEIGHT];

    private byte[] graphicBuffer = new byte[DISPLAY_ROW_NUM * DISPLAY_COL_NUM];


    public CPU() {
        indexRegister = 0x000;
        programCounter = MEMORY_START;
        stackPointer = 0x00;
        delayTimer = 0x00;
        soundTimer = 0x00;
        int[] fontInt = {
                0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                0x20, 0x60, 0x20, 0x20, 0x70, // 1
                0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        };
        for (int i = 0; i < fontInt.length; i++) {
            font[i] = (byte) fontInt[i];
        }
        loadFont();
    }


    public void run() {
        // One step run (what's the proper term for this?)
        short opcode = fetch();
        decodeExecute(opcode);
    }


    public void loadROM(String romFileName) throws IOException {
        Path path = Paths.get(romFileName);
        byte[] fileData = Files.readAllBytes(path);
        for (short i = MEMORY_START; i < (fileData.length + MEMORY_START); i++) {
            memory[i] = fileData[i - 0x200];
        }
    }

    private void loadFont() {
        for (short i = FONT_ADDR_START; i < (FONT_ADDR_START + FONT_LENGTH); i++) {
            memory[i] = font[i - FONT_ADDR_START];
        }
    }


    private short fetch() {
        // Get opcode (2 bytes) from memory
        byte hi = memory[programCounter];
        byte lo = memory[programCounter + 1];
        programCounter += 2;
        return (short) (((hi & 0xFF) << 8) | (lo & 0xFF));
    }

    private void decodeExecute(short opcode) {
        int opcodeClass = opcode & 0xF000;
        short opcodeArg = (short) (opcode & 0x0FFF);
        switch (opcodeClass) {
            case 0x0000 -> op0(opcodeArg);
            case 0x1000 -> op1(opcodeArg);
            case 0x6000 -> op6(opcodeArg);
            case 0x7000 -> op7(opcodeArg);
            case 0xA000 -> opA(opcodeArg);
            case 0xD000 -> opD(opcodeArg);
        }
    }

    private void op0(short arg) {
        if (arg == (short)0x0E0) {
            // Clear screen
            Arrays.fill(graphicBuffer, 0, graphicBuffer.length, (byte) 0);
        } else if (arg == (short)0x0EE) {
            // Return from subroutine
            --stackPointer;
            programCounter = stack[stackPointer];
        }
    }

    private void op1(short arg) {
        programCounter = arg;
    }

    private void op6(short arg) {
        // Set Vx register
        int x = (arg & 0x0F00) >> 8;
        byte val = (byte) (arg & 0x00FF);
        vRegister[x] = val;
    }


    private void op7(short arg) {
        // Add n to register Vx
        int x = arg & 0x0F00;
        byte nn = (byte) (arg & 0x00FF);
        vRegister[x] += nn;
    }


    private void opA(short arg) {
        // Set index register
        indexRegister = (short) (arg & 0x0FFF);
    }


    private void opD(short arg) {
        // Draw
        // todo: better variable name
        int x = (arg & 0x0F00) >> 8;
        int y = (arg & 0x00F0) >> 4;
        int n = (arg & 0x000F);
        int posX = vRegister[x] & (DISPLAY_COL_NUM - 1); // modulo 64, initial position is wrapped
        int posY = vRegister[y] & (DISPLAY_ROW_NUM - 1); // mod 32
        vRegister[0xF] = 0;
        for (int i = posY; i < Math.min(posY + n, DISPLAY_ROW_NUM); i++) {
            byte spriteRow = memory[indexRegister + (i - posY)];
            for (int j = posX; j < Math.min(posX + 8, DISPLAY_COL_NUM); j++) {
                int spritePixel = (spriteRow >> j) & 1; // sprite pixel di row i, bit ke-j
                int bufferPixel = getGraphicBufferAt(i, j);
                if (spritePixel == 1) {
//                    byte pixelSetValue = (byte) (getGraphicBufferAt(j, i) ^ 1);
//                    setGraphicBuffer((byte)0, j, i);
//                    vRegister[0xF] = (byte) 1;
                    if (bufferPixel == 1) {
                        vRegister[0xF] = (byte) 1;
                    }
                    byte pixelSetValue = (byte) (getGraphicBufferAt(i, j) ^ 1);
                    setGraphicBuffer(pixelSetValue, i, j);
                }
            }
        }
    }


    private int getGraphicBufferAt(int x, int y) {
        return graphicBuffer[(x * DISPLAY_ROW_NUM) + y];
    }

    private void setGraphicBuffer(byte data, int posX, int posY) {
        int flattenedIndex = (posX * DISPLAY_ROW_NUM) + posY;
        graphicBuffer[flattenedIndex] = data;
    }

//    public byte[] getGraphicBuffer() {
//        return graphicBuffer;
//    }

    public byte[][] getGraphicBuffer() {
        byte[][] temp = new byte[DISPLAY_ROW_NUM][DISPLAY_COL_NUM];
        for (int i = 0; i < DISPLAY_ROW_NUM; i++) {
            for (int j = 0; j < DISPLAY_COL_NUM; j++) {
                temp[i][j] = (byte) getGraphicBufferAt(i, j);
            }
        }
        return temp;
    }
}
