/**
 * Classe pour tout ce qui a trait la manipulation de données
 * (e.g. conversion, stuffing, padding..)
 */
public class DataManipulation {

    /**
     * Convertie un tableau de bytes en une séquence de bits
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

        while (bits.length() != 8) {
            bits = '0' + bits;
        }

        return bits;
    }

    /**
     * Convertie une séquence de bits en tableau de bytes
     * @param bin chaîne de caractères (séquence de bits)
     * @return tableau de bytes
     */
    public static byte[] binToBytes (String bin) {

        int bytes_n = (int) Math.ceil((float)bin.length() / 8); // nombre de bytes dans la séquence
        byte[] bytes =new byte[bytes_n];

        int j =0;
        for (int i = 0; i < bin.length(); i+=8) {
            if ((i+8) <= bin.length()) {
                bytes[j] = (byte) Integer.parseInt(bin.substring(i, i + 8), 2);
            } else {
                bytes[j] = (byte) Integer.parseInt(bin.substring(i, bin.length()));
            }
            j++;
        }
        return bytes;
    }

    /**
     * Concertie une séquence de 8 bits ou moins en un byte
     * @param bin chaîne de caractères
     * @return byte
     */
    public static byte binToByte (String bin) {
        return (byte) Integer.parseInt(bin, 2);
    }


    /**
     * Convertie une séquence de bits en entier
     * @param bin chaîne de caractères
     * @return entier
     */
    public static int binToInt (String bin) {
        return Integer.parseInt(bin, 2);
    }

    /**
     * Bit stuffing : insère des bit '0' entre chaque séquence de 5 bits '1' consécutive, afin
     * de ne pas confordre les données avec les flags
     * @param data chaînes de caractères représentant la séquence de bits
     * @return séquences de bits avec stuffing
     */
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

    /**
     * UnStuffing d'une séquence de bits : retire les bits '0' qui sont
     * précédés d'une séquence de 5 bits '1' consécutive
     * @param data chaînes de caractères représentant la séquence de bits
     * @return séquences de bits sans stuffing
     */
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

    /**
     * Flip un bit d'une séquence de bits à un index donné
     * @param bitSeq chaîne de caractères (séquence de bits)
     * @param index entier
     * @return séquence de bits avec 1 bit flippé (chaîne de caractères)
     */
    public static String bitFlip (String bitSeq, int index) {

        if (index >= bitSeq.length())
            return bitSeq;

        char bit = bitSeq.charAt(index);

        if (bit == '0')
            bit = '1';
        else
            bit = '0';

        return bitSeq.substring(0, index) + bit + bitSeq.substring(index+1);
    }
}
