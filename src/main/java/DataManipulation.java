public class DataManipulation {

    public static String bytesToBin(byte[] bts) {

        String bin = "";

        for (byte b : bts) {
            bin += Integer.toBinaryString(b);
        }

        return bin;
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



}
