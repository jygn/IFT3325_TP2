public class Frame {

    private static final String flag  = "01111110";
    private char type;
    private byte num;
    private String data;
    private String CRC;

    public Frame(char type, int num, String data) {

//        this.flag = flag;
        this.type = type;
        this.num = (byte) num;
        this.data = data;
//        this.CRC = CRC;

    }
}
