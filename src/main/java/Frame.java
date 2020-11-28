/**
 * Classe qui représente un trame respectant le format HDLC
 *
 * | Flag | Type | Num | Données | CRC | Flag |
 */
public class Frame {

    private static final String flag  = "01111110";
    private byte type;
    private byte num;
    private byte[] data;
    private String CRC;

    /**
     * Constructeur d'un trame
     * @param type byte
     * @param num entier
     * @param data tableau de bytes
     */
    public Frame(byte type, int num, byte[] data) {

        this.type = type;
        this.num = (byte) num;
        this.data = data;
        this.CRC = computeCRC();

    }

    /**
     * Constructeur d'un trame sans données
     * @param type caratère
     * @param num entier
     */
    public Frame(char type, int num) {
        this.type = (byte) type;
        this.num = (byte) num;
        this.data = null;
        this.CRC = computeCRC();
    }

    /**
     * Constructeur d'un trame à partir d'une représentation binaire
     * @param binFrame Chaîne de carcatère représentant une suite de bits
     */
    public Frame(String binFrame) {

        byte type = DataManipulation.binToByte(binFrame.substring(0, 8));
        this.type = type;

        int binSeq_size = binFrame.length();

        switch (type) {
            case 'I':
                this.num = (byte) DataManipulation.binToInt(binFrame.substring(8, 16));
                // data
                String dataString = binFrame.substring(16, binFrame.length() - 16);
                this.data = DataManipulation.binToBytes(dataString);
                this.CRC = binFrame.substring(binSeq_size - 16);
                break;
            case 'C':
            case 'A':
            case 'R':
            case 'P':
            case 'F':
                this.num = (byte) DataManipulation.binToInt(binFrame.substring(8, 16));
                this.CRC = binFrame.substring(binSeq_size - 16);
                break;

            default:
                System.out.println("FRAME error in frame");
        }
    }

    /**
     * Donne le numéro du trame
     * @return byte
     */
    public byte getNum() {
        return num;
    }

    /**
     * Donne le type du trame
     * @return byte
     */
    public byte getType() { return type; }

    /**
     * Donne les données du trame
     * @return tableau de bytes
     */
    public byte[] getData() { return data; }

    /**
     * Donne le flag du trame
     * @return chaîne de caractères
     */
    public String getFlag() { return flag; }

    /**
     * Représente un frame sous format binaire
     * @return chaîne de caracères
     */
    public String toBin () {
        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));

        if(this.data == null) return t+n+this.CRC; // some frame doesnt have data
        String d = DataManipulation.bytesToBin(this.data);

        return t+n+d+this.CRC;
    }

    /**
     * Donne le checksum calculé en utilisant CRC
     * @return chaîne de caractères
     */
    public String computeCRC() {

        String t = DataManipulation.bitsPadding(Integer.toBinaryString(this.type));
        String n = DataManipulation.bitsPadding(Integer.toBinaryString(this.num));

        if( this.data == null) return Checksum.calculCRC(t+n);
        String d = DataManipulation.bytesToBin(this.data);

        return Checksum.calculCRC(t+n+d);
    }

}
