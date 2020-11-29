/**
 * Classe permettant de calculer le checksum d'une chaîne de bits
 * à l'aide du polynôme générateur CRC-CCITT
 */
public class Checksum {

    // CRC-CCITT (x^16 + x^12 + x^5 + 1)
    private static final String POLYNOMIAL = "10001000000100001";

    /**
     * Calcul le checksum à l'aide du CRC
     * @param data chaîne de caractères (bits)
     * @return checksum (chaîne de caractères)
     */
    public static String calculCRC (String data) {
        String reminder = xor_div(data + getPolyPadding(POLYNOMIAL));
        return bitsPadding16(reminder);
    }

    /**
     * Donne le padding de '0' nécessaire pour le calcul du checksum
     * selon un polynôme générateur
     * @return padding de '0' (chaîne de caractères)
     */
    public static String getPolyPadding (String poly) {
        String padding = "";
        int r = poly.length();
        for (int i = 0; i < r-1 ; i++) {
            padding += '0';
        }
        return padding;
    }

    /**
     * Vérifie si la séquence de bits reçue n'est pas erronée
     * @param data séquence de bits (string)
     * @return  boolean
     */
    public static boolean containsError(String data) {
        boolean isWrong = false;
        if (!Checksum.xor_div(data).equals("0")) {
            isWrong = true;
        }
        return isWrong;
    }

    /**
     * Calcul la division XOR d'une séquence de bits par un polynôme générateur
     * @param data séquence de bits
     * @return séquence de bits représentant le reste de la division
     */
    public static String xor_div (String data) {

        int r = POLYNOMIAL.length();
        if (data.length() < r)
            return data;

        data = data.substring(zeroSeqCounter(data));    // trim les 0's au début
        if (data.equals("0")) return data;
        int i = 0;
        String reminder = "";
        int n_zeros = 0;

        while (i < r) { // première division qui va donner un 1er reminder

            if (data.charAt(i) == POLYNOMIAL.charAt(i))
                reminder += '0';
            else
                reminder += '1';

            i++;
        }

        reminder += data.substring(i);      // ajoute le reste de la sequence à diviser
        if (reminder.charAt(0) == '0') {
            reminder = reminder.substring(zeroSeqCounter(reminder));

        }
        i = 0;

        while (reminder.length() >= r) {

            if (reminder.charAt(i) == POLYNOMIAL.charAt(i))
                reminder = bitFlip(reminder, i, '0');
            else
                reminder = bitFlip(reminder, i, '1');

            i++;

            if (i >= r) {
                if (reminder.charAt(0) == '0') {    // trim les 0's au début
                    n_zeros = zeroSeqCounter(reminder);
                    if (n_zeros == reminder.length()) {   // 00000....
                        break;
                    }
                    reminder = reminder.substring(n_zeros);
                }
                i=0;
            }
        }

        return reminder;
    }

    /**
     * Compte la 1ère séquence de 0 de la sequence de bit avant d'atteindre le bit '1'
     * @param binSeq : séquence de bits (chaîne de caractères)
     * @return : le nb de 0's de la 1ere séquence
     */
    public static int zeroSeqCounter (String binSeq) {
        int i = 0;
        while ((binSeq.charAt(i) == '0') & (i < binSeq.length()-1))
            i++;
        return i;
    }

    /**
     * Flip le bit désiré à un index précis d'une séquence de bits
     * @param bitSeq séquence de bits (chaîne de caractères)
     * @param index entier
     * @param bit char
     * @return séquence de bits avec le bit flippé
     */
    public static String bitFlip (String bitSeq, int index, char bit) {
        return bitSeq.substring(0, index) + bit + bitSeq.substring(index+1);
    }

    /**
     * Ajoute le padding nécessaire pour avoir une séquence de 16 bits
     * @param bits chaîne de caractères (séquence)
     * @return chaîne de caractères (padding + séquence de bits)
     */
    public static String bitsPadding16 (String bits) {

        while (bits.length()  != 16) {
            bits = '0' + bits;
        }
        return bits;
    }
}


