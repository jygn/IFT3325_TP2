public class Frame {

    private static final String flag  = "01111110";
    private byte type;
    private byte num;
    private byte[] data;
    private String CRC;
    private String flag1;
    private String flag2;

    public Frame(byte type, int num, byte[] data) {

        this.type = type;
        this.num = (byte) num;
        this.data = data;
//        this.CRC = CRC;

    }

    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    public Frame(String flag1, byte type, int num, byte[] data, String flag2) {
        this.flag1 = flag1;
        this.type = type;
        this.num = (byte) num;
        this.data = data;
        this.flag2 = flag2;
    }

    /**
     * Représentation d'un frame sous format binaire
     * @return String : frame en format binaire
     */
    public String toBin () {
        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));
        String d = DataManipulation.bytesToBin(this.data);

        return t+n+d;
    }

    public String getFlag() {
        return flag;
    }
}
