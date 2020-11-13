import java.util.LinkedList;
import java.util.Queue;

public class FramesManager {

    Queue<Frame> framesFIFO;

    public FramesManager (String data) {
        framesFIFO = new LinkedList<Frame>() {};

        char type;
        int num;

        //TODO 1 arbitraire -> changer et mettre nb de trames selon data max
        for (int i = 0 ; i < 1; i++) {
            // test
            type = 'i';
            num = i%8;  //2^3 = 8 combinaisons
            // TODO crc = getcrc()...
            framesFIFO.add(new Frame(type, num, data));
        }
    }

    public Queue<Frame> getFramesFIFO() {
        return framesFIFO;
    }

}
