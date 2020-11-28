/**
 * Classe pour tout ce qui a trait la manipulation de données
 * (e.g. conversion, stuffing, padding..)
 */
public class DataManipulation {

    /**
     * Transforme un tableau de bytes en une séquence de bits
     * @param bts Tableau de bytes
     * @return chaîne de caractères
     */
    public static String bytesToBin(byte[] bts) {

        String bin = "";

        for (byte b : bts) {
            bin += bitsPadding(Integer.toBinaryString(b));
        }

        return bin;
    }

    /**
     * Ajoute le padding nécessaire pour avoir une séquence de 8 bits
     * @param bits chaîne de caractères (séquence)
     * @return chaîne de caractères (padding + séquence de bits)
     */
    public static String bitsPadding (String bits) {

        while (bits.length() % 8 != 0) {
            bits = '0' + bits;
        }

        return bits;
    }

    /**
     * Transforme une séquence de bits en tableau de bytes
     * @param bin chaîne de caractères (séquence de bits)
     * @return tableau de bytes
     */
    public static byte[] binToBytes (String bin) {

        int bytes_n = (int) Math.ceil((float)bin.length() / 8); // nombre de bytes dans la séquence
        byte[] bytes =new byte[bytes_n];

        int j =0;
        for (int i = 0; i < bin.length(); i+=8) {
            bytes[j++] = Byte.parseByte(bin.substring(i, i+8), 2);
        }
        return bytes;
    }

    public static byte binToByte (String bin) {
        return (byte) Integer.parseInt(bin, 2);
    }

    public static int binToInt (String bin) {
        int num;

        num = (int) Integer.parseInt(bin, 2);

        return num;
    }

    public static String bitStuffing (String data) {

        String seq = "";
        int c = 0;
        for (int i = 0; i < data.length(); i++) {

            char char_i = data.charAt(i);
            seq += char_i;

            if (char_i == '1')
                c++;
            else
                c = 0;

            if (c == 5) {
                seq += '0';
                c = 0;
            }

        }

        return seq;

    }

    public static String bitUnStuffing (String data) {

        String seq = "";
        int c = 0;

        for (int i = 0; i < data.length(); i++) {

            char char_i = data.charAt(i);
            if (c != 5)
                seq += char_i;

            if (char_i == '1')
                c++;
            else
                c = 0;

        }

        return seq;
    }

    public static String bitFlip (String bitSeq, int index) {
        char bit = bitSeq.charAt(index);
        if (bit == '0')
            bit = '1';
        else
            bit = '0';

        return bitSeq.substring(0, index) + bit + bitSeq.substring(index+1);
    }

    public static byte stringTobyte (String data){
        byte[] b = data.getBytes();
        return b[0];
    }

    /**
     * Split un tableau de bytes en un tableau de tableau de bytes
     * @param data : tableau de bytes
     * @param split_size : taille de chaque sous tableau
     * @return : tableau de tableau de bytes
     */
    public static byte[][] splitBytes (byte[][] data, int split_size) {

        int n = (int) Math.ceil((double) data.length / split_size); // nb de chunk

        byte[][] data_chunks = new byte[n][];
        byte[] data_chunk;
        int src_pos = 0;

        for (int i = 0; i < n; i++) {
            data_chunk = new byte[split_size];

            if (data.length - (i * split_size) < split_size) {  // last chunk
                data_chunk = new byte[data.length - (i * split_size)];
                System.arraycopy(data, src_pos, data_chunk, 0, data.length - (i * split_size));
            } else {
                System.arraycopy(data, src_pos, data_chunk, 0, split_size);
            }

            data_chunks[i] = data_chunk;
            src_pos += split_size;
        }
        return data_chunks;
    }

    public static byte[] trimBytes(byte[] data) {

        int nozeros = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != 0) nozeros++;
        }
        int i =0;
        byte[] newData = new byte[nozeros];
        for (int j = 0; j < data.length; j++) {
            if (data[j] != 0) newData[i++] = data[j];
        }
        return newData;
    }

}
