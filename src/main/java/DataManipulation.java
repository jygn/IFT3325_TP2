public class DataManipulation {

    public static String bytesToBin(byte[] bts) {

        String bin = "";

        for (byte b : bts) {
            bin += bitsPadding(Integer.toBinaryString(b));
        }

        return bin;
    }

    public static String bitsPadding (String bits) {

        while (bits.length() != 8) {
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

    public static String bitStuffing (String data) {

        String seq = "";
        int c = 0;
        for (int i = 0; i < data.length(); i++) {

            char char_i = data.charAt(i);
            seq += char_i;

            if (char_i == '1') {
                c++;
            } else {
                c = 0;
            }

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

            if (char_i == '1') {
                c++;
            } else {
                c = 0;
            }
        }

        return seq;
    }


    public static void main(String args[]){

        byte[] test = {'a','l','l','o'};
        String bin = DataManipulation.bytesToBin(test);
        bin += "11111111";
        System.out.println(bin);

        String stuffed = bitStuffing(bin);
        System.out.println(stuffed);
        String unstuff = bitUnStuffing(stuffed);
        System.out.println(unstuff);

        System.out.println(DataManipulation.binToText(bin));

    }


}
