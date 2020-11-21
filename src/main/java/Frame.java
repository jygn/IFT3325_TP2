import javax.xml.crypto.Data;

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

    //different type of frame
    public Frame(String type, int num) {

        this.type = DataManipulation.stringTobyte(type);
        this.num = (byte)num;
        this.data = null;
        this.CRC = computeCRC();
    }

    public Frame(String binFrame) {

        //TODO switch selon le type -> construit le frame selon son type
        String type = this.getFrameTypeInString(DataManipulation.binToByte(binFrame.substring(0, 8)));

        switch (type) {
            case "I":
                this.type = DataManipulation.binToByte(binFrame.substring(0, 8));
                this.num = (byte) DataManipulation.binToInt(binFrame.substring(8, 16));
                //data
                String dataString = binFrame.substring(16, binFrame.length() - 16);
                this.data = DataManipulation.binToBytes(dataString, dataString.length());
                this.CRC = binFrame.substring(binFrame.length() - 16);
                break;

            case "C":
            case "F":
            case "A":
                this.type = DataManipulation.binToByte(binFrame.substring(0, 8));
                this.num = (byte) DataManipulation.binToInt(binFrame.substring(8, 16));
                this.CRC = binFrame.substring(binFrame.length() - 16);
                break;

            default:
                System.out.println("error in frame");
        }

    }


    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    public byte getType() { return type; }

    private String getFrameTypeInString(byte bits){
        return DataManipulation.byteToString(bits);
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getTypeInString(){
        return DataManipulation.byteToString(this.type);
    }

    public Frame(String flag1, byte type, int num, byte[] data, String CRC, String flag2) {
        this.flag1 = flag1;
        this.type = type;
        this.num = (byte) num;
        this.data = data;
        this.CRC = CRC;
        this.flag2 = flag2;
    }

    /**
     * Représentation d'un frame sous format binaire
     * @return String : frame en format binaire
     */
    public String toBin () {
        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));

        if(this.data == null) return t+n; //some frame doesnt have data
        String d = DataManipulation.bytesToBin(this.data);

        return t+n+d+this.CRC;
    }


    public String computeCRC() {

        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));

        if( this.data == null) return Checksum.calculCRC(t+n);
        String d = DataManipulation.bytesToBin(this.data);

        return Checksum.calculCRC(t+n+d);
    }


    public String getFlag() {
        return flag;
    }
}
