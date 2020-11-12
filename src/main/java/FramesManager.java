import java.util.LinkedList;
import java.util.Queue;

public class FramesManager {

    Queue<Frame> framesFIFO;

    public FramesManager (byte[] data, int max_size) {
        framesFIFO = new LinkedList<>() {};

        for (int i=0; i<max_size; i++) {
//            Frame f = new Frame((int) "i", )
//            framesFIFO.push();
        }
    }

}
