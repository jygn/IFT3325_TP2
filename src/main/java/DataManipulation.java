import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DataManipulation {

    public static String bytesToBin(byte[] bts) {

        String bin = "";

        for (byte b : bts) {
            bin += bitsPadding(Integer.toBinaryString(b));
        }

        return bin;
    }

    public static String byteToString(byte bits){
        return new String(new byte[] {bits}, StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] bytes) {
        String text = "";
        for (byte b : bytes) {
            text += byteToString(b);
        }
        return text;
    }

    public static String bitsPadding (String bits) {

        while (bits.length() % 8 != 0) {
            bits = '0' + bits;
        }

        return bits;
    }

    public static String binToText (String bin) {
        String text = "";

        for (int i = 0; i < bin.length(); i+=8) {
            text += (char) Integer.parseInt(bin.substring(i, i+8), 2);
        }

        return text;
    }

    public static byte[] binToBytes (String bin, int size) {
        byte[] bytes =new byte[size];

        for (int i = 0; i < bin.length(); i+=8) {
            bytes[i] = (byte) Integer.parseInt(bin.substring(i, i+8), 2);
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

    public static void main(String args[]){

        byte test = 0;
        System.out.println(DataManipulation.byteToString(test));

    }


}
