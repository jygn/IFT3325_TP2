public class Frame {

    private static final String flag  = "01111110";
    private byte type;
    private byte num;
    private byte[] data;
    private String CRC;

    public Frame(byte type, int num, byte[] data) {

        this.type = type;
        this.num = (byte) num;
        this.data = data;
//        this.CRC = CRC;

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
