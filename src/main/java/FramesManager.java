import java.util.ArrayList;

public class FramesManager {

    private ArrayList<Frame> framesList;

    public void createFramesList (byte[][] data, int numberOfFrame) {

        framesList = new ArrayList<>();
        byte type;
        int num;
        Frame f;
        for (int i = 0; i < data.length; i++) {
            type = 'I';
            num = i % numberOfFrame;
            f = new Frame(type, num, data[i]);
            framesList.add(f);
        }
    }

    public ArrayList<Frame> getFramesList() { return this.framesList; }

    public Frame getFrameConnectionConfirmation(int frame_num) {

        // go-back-N request
        if(frame_num == 0){
            //send RR0 -> is waiting for the first frame
            return new Frame('A',0);

        } else { //not supported
            //send an end of communication
            return new Frame('F', 0);
        }
    }

    public Frame getFrameByType (byte type, int frame_num) {

        switch (type) {
            case 'I': // information
                // ack is the number of the frame + 1
                return new Frame('A', (frame_num + 1)%Sender.NUMBER_OF_FRAME);
            case 'C': // Connection request
                return getFrameConnectionConfirmation(frame_num);
            case 'F': // end of communication
                return new Frame('F', 0);
            case 'P':
                return new Frame('A', frame_num);
            default:
                return new Frame('R', frame_num);
        }
    }

    public String frameExtract (String input) {
        input = input.substring(8, input.length() - 8);    // without flags
        return DataManipulation.bitUnStuffing(input);    // remove bit stuffing
    }

}
