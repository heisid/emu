package id.my.rosyidharyadi.Model;

import static id.my.rosyidharyadi.Constant.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static id.my.rosyidharyadi.Model.Utility.*;
import static java.util.Map.entry;

public class CPU {
    private byte[] memory = new byte[MEMORY_SIZE];
    private byte[] vRegister = new byte[V_REGISTER_SIZE];
    private short indexRegister;
    private short programCounter;

    private short[] stack = new short[STACK_SIZE];

    private byte delayTimer;
    private byte soundTimer;
    private long timerLastTime;
    private byte[] font = new byte[FONT_LENGTH * FONT_HEIGHT];

    private byte[] graphicBuffer = new byte[DISPLAY_ROW_NUM * DISPLAY_COL_NUM];

    private final Keyboard keyboard;


    public CPU(Keyboard keyboard) {
        indexRegister = 0x000;
        programCounter = MEMORY_START;
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
        this.keyboard = keyboard;
        timerLastTime = System.nanoTime();
    }


    public void run() {
        short opcode = fetch();
        decodeExecute(opcode);
        timerUpdate();
    }


    private void timerUpdate() {
        long now = System.nanoTime();
        long timeDelta = now - timerLastTime; // 1/60 second
        timerLastTime = now;
        if (timeDelta > 16670000) {
            if (soundTimer > 0) {
                soundTimer--;
            }
            if (delayTimer > 0) {
                delayTimer--;
            }
        }
    }


    public void loadROM(String romFileName) throws IOException {
        Path path = Paths.get(romFileName);
        byte[] fileData = Files.readAllBytes(path);
        for (short i = MEMORY_START; i < (fileData.length + MEMORY_START); i++) {
            memory[i] = fileData[i - 0x200];
        }
    }

    private void loadFont() {
        for (short i = FONT_ADDR_START; i < (FONT_ADDR_START + (FONT_LENGTH * FONT_HEIGHT)); i++) {
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
            case 0x2000 -> op2(opcodeArg);
            case 0x3000 -> op3(opcodeArg);
            case 0x4000 -> op4(opcodeArg);
            case 0x5000 -> op5(opcodeArg);
            case 0x6000 -> op6(opcodeArg);
            case 0x7000 -> op7(opcodeArg);
            case 0x8000 -> op8(opcodeArg);
            case 0x9000 -> op9(opcodeArg);
            case 0xA000 -> opA(opcodeArg);
            case 0xB000 -> opB(opcodeArg);
            case 0xC000 -> opC(opcodeArg);
            case 0xD000 -> opD(opcodeArg);
            case 0xE000 -> opE(opcodeArg);
            case 0xF000 -> opF(opcodeArg);
        }
    }

    private void stackPush(short val) {
        for (int i = stack.length - 2; i > 0; i--) {
            stack[i] = stack[i - 1];
        }
        stack[0] = val;
    }

    private short stackPop() {
        short poppedVal = stack[0];
        for (int i = 1; i < stack.length; i++) {
            stack[i - 1] = stack[i];
        }
        stack[stack.length - 1] = 0;
        return poppedVal;
    }

    private void op0(short arg) {
        if (arg == (short)0x0E0) {
            // Clear screen
            Arrays.fill(graphicBuffer, 0, graphicBuffer.length, (byte) 0);
        } else if (arg == (short)0x0EE) {
            programCounter = stackPop();
        }
    }

    private void op1(short arg) {
        programCounter = arg;
    }

    private void op2(short arg) {
        // Routine call
        stackPush(programCounter);
        programCounter = arg;
    }

    private void op3(short arg) {
        // Skip if equal
        int x = (arg & 0xF00) >> 8;
        byte nn = byteFromUi(arg & 0x0FF);
        if (vRegister[x] == nn) {
            programCounter += 2;
        }
    }

    private void op4(short arg) {
        // Skip if not equal
        int x = (arg & 0xF00) >> 8;
        byte nn = byteFromUi(arg & 0x0FF);
        if (vRegister[x] != nn) {
            programCounter += 2;
        }
    }

    private void op5(short arg) {
        // Skip if equal
        int x = (arg & 0xF00) >> 8;
        int y = (arg & 0x0F0) >> 4;
        if (vRegister[x] == vRegister[y]) {
            programCounter += 2;
        }
    }

    private void op6(short arg) {
        // Set Vx register
        int x = (arg & 0x0F00) >> 8;
        byte val = (byte) (arg & 0x00FF);
        vRegister[x] = val;
    }


    private void op7(short arg) {
        // Add n to register Vx
        int x = (arg & 0x0F00) >> 8;
        byte nn = (byte) (arg & 0x00FF);
        vRegister[x] += nn;
    }

    private void op8(short arg) {
        // Binary and arithmetic operations
        int subtype = arg & 0x00F;
        int x = (arg & 0xF00) >> 8;
        int y = (arg & 0x0F0) >> 4;
        int intVx = byte2Ui(vRegister[x]);
        int intVy = byte2Ui(vRegister[y]);
        switch (subtype) {
            case 0x0 -> vRegister[x] = vRegister[y];
            case 0x1 -> vRegister[x] |= vRegister[y];
            case 0x2 -> vRegister[x] &= vRegister[y];
            case 0x3 -> vRegister[x] ^= vRegister[y];
            case 0x4 -> {
                int resAdd = intVx + intVy;
                vRegister[x] = byteFromUi(resAdd);
                vRegister[0xF] = resAdd > 255 ? (byte) 1 : (byte) 0;
            }
            case 0x5 -> {
                vRegister[x] = (byte)(intVx - intVy);
                vRegister[0xF] = intVx > intVy ? (byte) 1 : (byte) 0;
            }
            case 0x6 -> {
                // Note: Setting Vx to Vy was done in original COSMAC VIP
                // But in modern machines (starting in 1990s) this isn't
                // the case anymore.
                // todo: make it configurable to be compatible with the old one
//                vRegister[x] = vRegister[y];
                // wait, this getBit below is silly,
                // but for the sake of consistency with case 0xE i'll keep it
                byte tempVx = vRegister[x];
                vRegister[x] = (byte) ((vRegister[x] & 0xFF) >> 1);
                vRegister[0xF] = (byte) getBit(tempVx, 0);
            }
            case 0x7 -> {
                vRegister[x] = byteFromUi(intVy - intVx);
                vRegister[0xF] = intVy > intVx ? (byte) 1 : (byte) 0;
            }
            case 0xE -> {
                // Note: same as case 0x6
                byte tempVx = vRegister[x];
                vRegister[x] = (byte) ((vRegister[x] & 0xFF) << 1);
                vRegister[0xF] = (byte) getBit(tempVx, 7);
            }
            default -> {
            }
        }
    }

    private void op9(short arg) {
        // Skip if not equal
        int x = (arg & 0xF00) >> 8;
        int y = (arg & 0x0F0) >> 4;
        if (vRegister[x] != vRegister[y]) {
            programCounter += 2;
        }
    }

    private void opA(short arg) {
        // Set index register
        indexRegister = arg;
    }

    private void opB(short arg) {
        // Another ambigous instruction
        // Below is COSMAC VIP compatible. Todo: configurable
        programCounter = (short)(vRegister[0x0] + arg);
    }

    private void opC(short arg) {
        // Random
        int x = (arg & 0xF00) >> 8;
        int nn = arg & 0x0FF;
        int randNum = ThreadLocalRandom.current().nextInt(0, nn + 1);
        vRegister[x] = (byte) (randNum & nn);
    }


    private void opD(short arg) {
        // Draw
        // todo: better variable name
        int x = (arg & 0x0F00) >> 8;
        int y = (arg & 0x00F0) >> 4;
        int n = (arg & 0x000F);
        int posX = byte2Ui(vRegister[x]) % 63;
        int posY = byte2Ui(vRegister[y]) % 31;
        vRegister[0xF] = 0;
        for (int i = posY; i < Math.min(posY + n, DISPLAY_ROW_NUM); i++) {
            byte spriteRow = memory[indexRegister + (i - posY)];
            for (int j = posX; j < Math.min(posX + 8, DISPLAY_COL_NUM); j++) {
                int spritePixel = (spriteRow >> (7 - (j - posX))) & 1; // sprite pixel di row i, bit ke-j
                int bufferPixel = getGraphicBufferAt(i, j);
                if (spritePixel == 1) {
                    if (bufferPixel == 1) {
                        vRegister[0xF] = (byte) 1;
                    }
                    byte pixelSetValue = (byte) (getGraphicBufferAt(i, j) ^ 0xFF);
                    setGraphicBuffer(pixelSetValue, i, j);
                }
            }
        }
    }


    private void opE(short arg) {
        int subtype = arg & 0x0FF;
        int x = (arg & 0xF00) >> 8;
        int keyBeingPressed = keyboard.getKeyBeingPressed();
        int vX = vRegister[x];

        switch (subtype) {
            case 0x9E -> {
                if (vX == keyBeingPressed) {
                    programCounter += 2;
                }
            }
            case 0x1A -> {
                if (vX != keyBeingPressed && vX != -1) {
                    programCounter += 2;
                }
            }
        }
    }


    private void opF(short arg) {
        int subtype = arg & 0x0FF;
        int x = (arg & 0xF00) >> 8;

        switch (subtype) {
            // Timer related opcodes
            case 0x07 -> {
                vRegister[x] = delayTimer;
            }
            case 0x15 -> {
                delayTimer = vRegister[x];
            }
            case 0x18 -> {
                soundTimer = vRegister[x];
            }

            // Index addition
            case 0x1E -> {
                int resAdd = byte2Ui(vRegister[x]) + short2Ui(indexRegister);
                if (resAdd > 0xFFF) { // Amiga behavior of "overflow"
                    vRegister[0xF] = 1;
                }
                indexRegister = shortFromUi(resAdd);
            }

            // Get key (blocking behaviour)
            case 0x0A -> {
                // Halt execution until a key pressed
                int currentKey = keyboard.getKeyBeingPressed();
                if (currentKey == -1) {
                    programCounter -= 2;
                } else {
                    vRegister[x] = byteFromUi(currentKey);
                }
            }

            // Get Font
            case 0x29 -> {
                int fontIdx = vRegister[x] & 0x0F; // get last nibble
                indexRegister = shortFromUi(FONT_ADDR_START + (fontIdx * FONT_HEIGHT));
            }

            // BCD Conversion
            case 0x33 -> {
                int val = byte2Ui(vRegister[x]);
                int idx = 2;
                while (val > 0) {
                    memory[indexRegister + idx] = byteFromUi(val % 10);
                    val = val / 10;
                    idx--;
                }
            }

            // Memory Store
            case 0x55 -> {
                System.arraycopy(vRegister, 0, memory, indexRegister, x + 1);
            }

            // Memory Load
            case 0x65 -> {
                System.arraycopy(memory, indexRegister, vRegister, 0, x + 1);
            }
        }
    }


    private int getGraphicBufferAt(int x, int y) {
        return graphicBuffer[(y * DISPLAY_ROW_NUM) + x];
    }

    private void setGraphicBuffer(byte data, int posX, int posY) {
        int flattenedIndex = (posY * DISPLAY_ROW_NUM) + posX;
        graphicBuffer[flattenedIndex] = data;
    }


    public byte[][] getGraphicBuffer() {
        byte[][] temp = new byte[DISPLAY_ROW_NUM][DISPLAY_COL_NUM];
        for (int i = 0; i < DISPLAY_ROW_NUM; i++) {
            for (int j = 0; j < DISPLAY_COL_NUM; j++) {
                temp[i][j] = (byte) getGraphicBufferAt(i, j);
            }
        }
        return temp;
    }

    // Buat debugging aja
    public HashMap<String, String> getState() {
        HashMap<String, String> debugInfo =  new HashMap<>(
                    Map.ofEntries(
                            entry("PC", showHex(programCounter)),
                            entry("I", showHex(indexRegister)),
                            entry("DT", showHex(delayTimer)),
                            entry("ST", showHex(soundTimer)),
                            entry("Stack", Arrays.toString(stack))
                    )
        );
        for (int i = 0; i < vRegister.length; i++) {
            debugInfo.put("v" + showHex(i), showHex(vRegister[i]));
        }

        return debugInfo;
    }
}
