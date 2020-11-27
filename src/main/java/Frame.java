import javax.xml.crypto.Data;

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
        this.CRC = computeCRC();

    }

    //different type of frame
    public Frame(char type, int num) {

        this.type = (byte) type;
        this.num = (byte) num;
        this.data = null;
        this.CRC = computeCRC();
    }

    public Frame(String binFrame) {

        byte type = DataManipulation.binToByte(binFrame.substring(0, 8));

        switch (type) {
            case 'I':
                this.type = type;
                this.num = (byte) DataManipulation.binToInt(binFrame.substring(8, 16));
                //data
                String dataString = binFrame.substring(16, binFrame.length() - 16);
                this.data = DataManipulation.binToBytes(dataString, dataString.length());
                this.data = DataManipulation.trimBytes(this.data);  // trim les zeros inutiles
                this.CRC = binFrame.substring(binFrame.length() - 16);
                break;
            case 'C':
            case 'A':
            case 'R':
            case 'P':
            case 'F':
                this.type = type;
                this.num = (byte) DataManipulation.binToInt(binFrame.substring(8, 16));
                this.CRC = binFrame.substring(binFrame.length() - 16);
                break;

            default:
                System.out.println("FRAME error in frame");
        }

    }

    public byte getNum() {
        return num;
    }

    public byte getType() { return type; }

    public byte[] getData() { return data; }

    /**
     * Représentation d'un frame sous format binaire
     * @return String : frame en format binaire
     */
    public String toBin () {
        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));

        if(this.data == null) return t+n+this.CRC; //some frame doesnt have data
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
