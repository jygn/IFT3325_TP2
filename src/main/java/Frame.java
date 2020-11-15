public class Frame {

    private static final String flag  = "01111110";
    private char type;
    private byte num;
    private String data;
    private String CRC;

    public Frame(char type, int num, String data) {

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
        String t = Integer.toBinaryString(this.type);
        String n = Integer.toBinaryString(this.num);
        String d = Utility.stringToBin(this.data);

        return t+n+d;
    }

    public String getFlag() {
        return flag;
    }
}
