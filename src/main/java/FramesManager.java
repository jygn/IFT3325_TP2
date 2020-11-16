import java.util.ArrayList;

public class FramesManager {

    private static final int data_size = 1000;  // 1000 byte = 1Kb  // TODO data frame size ou frame size au complet?
    ArrayList<Frame> framesList;

    public FramesManager (byte[] data) {
        framesList = new ArrayList<Frame>() {};

        byte type;
        int num;

        int n = (int)Math.ceil((double) data.length / data_size); // nb de frame TODO: revoir..
        byte[] data_chunk;
        int src_pos = 0;

        for (int i = 0 ; i < n; i++) {
            type = 'i'; // test
            num = i%8;  //2^3 = 8 combinaisons
            // TODO crc = getcrc()...

            data_chunk = new byte[data_size];
            if (data.length - (i*data_size) < data_size) // last data chunk
                System.arraycopy(data, src_pos, data_chunk, 0, data.length - (i * data_size));
            else
                System.arraycopy(data, src_pos, data_chunk, 0, data_size);

            framesList.add(new Frame(type, num, data_chunk));
            src_pos += data_size;
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
