package id.my.rosyidharyadi.Model;

public class Utility {
    public static String showHex(int n) {
        return String.format("%x%n", n);
    }

    public static String showHex(short n) {
        return String.format("%x%n", n);
    }

    public static String showHex(byte n) {
        return String.format("%x%n", n);
    }

    public static int byte2Ui(byte val) {
        return val & 0xFF;
    }

    public static byte byteFromUi(int val) {
        return (byte) (val & 0xFF);
    }

    public static int short2Ui(short val) {
        return val & 0xFFFF;
    }

    public static short shortFromUi(int val) {
        return (short) (val & 0xFFFF);
    }

    public static int getBit(byte val, int pos) {
        return (val & (0x01 << pos)) >> pos;
    }
}
