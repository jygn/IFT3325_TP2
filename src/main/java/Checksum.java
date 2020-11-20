public class Checksum {

    //CRC-CCITT (x^16 + x^12 + x^5 + 1)
    private final String POLYNOMIAL = "10001000000100001";


    public String getPadding () {
        String padding = "";
        int r = POLYNOMIAL.length();
        for (int i = 0; i < r-1 ; i++) {
            padding += '0';
        }
        return padding;
    }

    public String xor_div (String data) {

        data = data.substring(zeroSeqCounter(data));    // trim les 0's au début

        int i = 0;
        String reminder = "";
        int r = POLYNOMIAL.length();

        while (i < r) {

            if (data.charAt(i) == POLYNOMIAL.charAt(i))
                reminder += '0';
            else
                reminder += '1';

            i++;
        }

        reminder += data.substring(i);
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
                if (reminder.charAt(0) == '0') {
                    reminder = reminder.substring(zeroSeqCounter(reminder));
                }
                i=0;
            }
        }

        return reminder;
    }

    public int zeroSeqCounter (String data) {
        int c = 0;
        int i = 0;
        while (data.charAt(i++) == '0') c++;
        return c;
    }

    public String bitFlip (String bitSeq, int index, char bit) {
        return bitSeq.substring(0, index) + bit + bitSeq.substring(index+1);
    }

    public static void main(String args[]){

        Checksum chk = new Checksum();

        byte[] data = {'a','l', 'l','o'};
//        String bin = DataManipulation.bytesToBin(data);
        String bin = "1000000000000000";
        bin = bin + chk.getPadding();
        System.out.println(bin);
        String reminder = chk.xor_div(bin);
        System.out.println(DataManipulation.bitsPadding(reminder)); // TODO faire le padding à la fin pour connaitre le msg

    }
}


