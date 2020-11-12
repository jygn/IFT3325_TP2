public class Frame {
    private byte flag;
    private byte type;
    private byte num;
    private byte[] data;
    private short CRC;

    public Frame(byte type, byte num, byte[] data) {
//        this.flag = flag;
        this.type = type;
        this.num = num;
        this.data = data;
//        this.CRC = CRC;

    }
}
