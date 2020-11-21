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
        this.CRC = computeCRC();

    }

    public Frame(String binFrame) {

        this.type = DataManipulation.binToByte(binFrame.substring(0,8));
        this.num = (byte) DataManipulation.binToInt(binFrame.substring(8,16));

        //data
        String dataString = binFrame.substring(16, binFrame.length()-16);
        this.data = DataManipulation.binToBytes(dataString, dataString.length());

        this.CRC = binFrame.substring(binFrame.length()-16);

    }

    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    /**
     * Représentation d'un frame sous format binaire
     * @return String : frame en format binaire
     */
    public String toBin () {
        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));
        String d = DataManipulation.bytesToBin(this.data);

        return t+n+d+this.CRC;
    }


    public String computeCRC() {
        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));
        String d = DataManipulation.bytesToBin(this.data);

        return Checksum.xor_div(t+n+d);
    }

    /**
     * Vérifie si le frame reçue n'est pas erroné
     * @param data : frame reçu sous format binaire (string)
     * @return : boolean
     */
    public boolean errorCheck(String data) {
        boolean isWrong = false;
        if (!Checksum.xor_div(data).equals("0")) {
            isWrong = true;
        }
        return isWrong;
    }

    public String getFlag() {
        return flag;
    }
}
