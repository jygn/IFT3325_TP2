import java.util.ArrayList;

public class FramesManager {

    ArrayList<Frame> framesList;

    public FramesManager (byte[] data) {
        framesList = new ArrayList<Frame>() {};

        byte type;
        int num;

        //TODO 1 arbitraire -> changer et mettre nb de trames selon data max
        // data.size() / nb max = nb de frames...
        for (int i = 0 ; i < 1; i++) {
            type = 'i'; // test
            num = i%8;  //2^3 = 8 combinaisons
            // TODO crc = getcrc()...
            framesList.add(new Frame(type, num, data));
        }
    }

    public String bitStuffing (String data) {

        String stuffed = "";
        int c = 0;
        for (int i = 0; i < data.length(); i++) {

            stuffed += data.charAt(i);

            if (data.charAt(i) == '1') {
                c++;
            } else {
                c = 0;
            }

            if (c == 5) {
                stuffed += '0';
                c = 0;
            }

        }

        return stuffed;

    }

    public ArrayList<Frame> getFramesList() {
        return framesList;
    }



}
