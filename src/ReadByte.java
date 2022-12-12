public class ReadByte {
    private final int b;
    private int turn;

    public ReadByte(int b) {
        this.b = b;
        turn = 7;
    }

    public boolean hasRemainingBits() {
        return turn > -1;
    }

    public int getNextBit() {
        if(turn == -1) return -1;
        int bit = (b & (1 << turn)) >> turn;
        turn--;
        return bit;
    }

    public static int getIthByte(long num, int i) {
        long mask = 0xFF;
        return (int) ((num & (mask << 8*i)) >> 8*i);
    }
}
