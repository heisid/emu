package id.my.rosyidharyadi.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CPU {
    private byte[] memory = new byte[4096];
    private short MEM_START = 0x200;

    private byte[] vRegister = new byte[16];
    private short indexRegister;
    private short programCounter;

    private short[] stack = new short[16];
    private byte stackPointer;

    private byte delayTimer;
    private byte soundTimer;
    private int FONT_LENGTH = 16;
    private short FONT_ADDR_START = 0x50;
    private byte[] font = new byte[FONT_LENGTH];

    private byte[] graphicBuffer = new byte[64 * 32]; // todo: this is not cool


    public CPU() {
        indexRegister = 0x000;
        programCounter = MEM_START;
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
        for (short i = MEM_START; i < (fileData.length + MEM_START); i++) {
            memory[i] = fileData[i - 0x200];
        }
    }

    private void loadFont() {
        for (short i = FONT_ADDR_START; i < (FONT_ADDR_START + FONT_LENGTH); i++) {
            memory[i] = font[i - FONT_ADDR_START];
        }
    }

    public void execute(byte opcode) {

    }

    public byte[] getGraphicBuffer() {
        return graphicBuffer;
    }
}
