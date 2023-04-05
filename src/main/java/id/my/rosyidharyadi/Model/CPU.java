package id.my.rosyidharyadi.Model;

import static id.my.rosyidharyadi.Constant.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CPU {
    private byte[] memory = new byte[MEMORY_SIZE];
    private byte[] vRegister = new byte[V_REGISTER_SIZE];
    private short indexRegister;
    private short programCounter;

    private short[] stack = new short[STACK_SIZE];
    private byte stackPointer;

    private byte delayTimer;
    private byte soundTimer;
    private byte[] font = new byte[FONT_LENGTH];

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
        for (short i = 0; i < fontInt.length; i++) {
            font[i] = (byte) fontInt[i];
        }
    }


    public void run() {
        // gw masih bingung enaknya gimana
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

    public void execute(byte opcode) {
        byte opcodeClass = (byte) (opcode & 0xF000);
        byte opcodeArg = (byte) (opcode & 0x0FFF);
        switch (opcodeClass) {
            case (byte)0x000:
                op0(opcodeArg);
                break;
        }
    }

    private void op0(byte arg) {
        if (arg == (byte)0x0E0) {
            // Clear screen
            Arrays.fill(graphicBuffer, 0, graphicBuffer.length, (byte) 0);
        } else if (arg == (byte)0x0EE) {
            // Return from subroutine
            --stackPointer;
            programCounter = stack[stackPointer];
        }
    }


    private void setGraphicBuffer(byte[] data) {
        System.arraycopy(data, 0, graphicBuffer, 0, graphicBuffer.length);
    }

    private void setGraphicBuffer(byte data, int posX, int posY) {
        int flattenedIndex = (posX * DISPLAY_ROW_NUM) + posY;
        graphicBuffer[flattenedIndex] = data;
    }

    public byte[] getGraphicBuffer() {
        return graphicBuffer;
    }
}
