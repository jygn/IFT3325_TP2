import javax.xml.crypto.Data;

public class Checksum {

    //CRC-CCITT (x^16 + x^12 + x^5 + 1)
    private static final String POLYNOMIAL = "10001000000100001";

    public static String calculCRC (String msg) { // msg : 1000 0000 0000 0000
        String reminder = xor_div(msg + getPadding());
        return DataManipulation.bitsPadding(reminder);  // reminder : 0001 1011 1001 1000
    }

    public static String getPadding () {
        String padding = "";
        int r = POLYNOMIAL.length();
        for (int i = 0; i < r-1 ; i++) {
            padding += '0';
        }
        return padding;
    }

    public static String xor_div (String data) {


        int r = POLYNOMIAL.length();
        if (data.length() < r)
            return data;

        data = data.substring(zeroSeqCounter(data));    // trim les 0's au début
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
     * Compte la 1ère séquence de 0 de la sequence de bit
     * @param binSeq : representation binaire en string
     * @return : le nb de 0's de la 1ere sequence
     */
    public static int zeroSeqCounter (String binSeq) {
        int i = 0;
        while ((binSeq.charAt(i) == '0') & (i < binSeq.length()-1))
            i++;
        return i;
    }

    public static String bitFlip (String bitSeq, int index, char bit) {
        return bitSeq.substring(0, index) + bit + bitSeq.substring(index+1);
    }

    public static void main(String args[]){

        Checksum chk = new Checksum();

        byte[] data = {'a','l', 'l','o'};
        String bin = "1000000000000000";
//        String tosend = chk.calculCRC(bin);
//        System.out.println(tosend);
//
//        System.out.println(chk.xor_div(tosend));


    }
}


