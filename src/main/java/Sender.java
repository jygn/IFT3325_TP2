import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;


public class Sender extends Thread{

    private String address = "";
    private int port;
    private Socket socket = null;
    private int connectionType;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private FramesManager fm;
    private String fileName;
    private ArrayList<String> binFrameList;

    private int windowMin; // inferior limit of the window
    private int windowMax; //upper limit of the window
    private int windowIndex; // at where we are in the list
    private boolean allFrameSent;
    private boolean closeConfirmation;
    private String input;
    private Frame frameInput;
    private String frameToSend;
    private boolean poll_req;
    private GBNTester tester;

    public static boolean TimeOutError = false;
    public static boolean BIT_FLIP = false;
    public static final int NUMBER_OF_FRAME = 8; // 2^3
    public static final int WINDOW_SIZE = NUMBER_OF_FRAME - 1;// (2^3) - 1 = 7
    public static final int TIME_OUT_INTERVAL = 3; // 3 seconds time out in go-back-N

    /**
     * Constructor
     * @param address address IP of the receiver
     * @param port port to communcate with the receiver
     */
    public Sender(String address, int port, String fileName, int connectionType){
        this.address = address;
        this.port = port;
        this.fileName = fileName;
        this.connectionType = connectionType;

        windowMin = 0; // inferior limit of the window
        windowMax = WINDOW_SIZE - 1; //upper limit of the window
        windowIndex = 0; // at where we are in the list
        allFrameSent = false;
        closeConfirmation = false;
        poll_req = false;

        tester = new GBNTester();

    }

    /**
     * connection between the sender and the receiver, Sender send a request for a go-back-N connection.
     */
    public void setupConnection() {

        try {
            Frame connectionFrame = new Frame('C', connectionType);
            out.writeUTF(fm.getFrameToSend(connectionFrame));
            out.flush();

            //wait for confirmation of the connection
            input = in.readUTF();
            input = fm.frameExtract(input);
            frameInput = new Frame(input);

            if(frameInput.getType() == 'A' && frameInput.getNum() == 0){
                System.out.println("SENDER connection go-back-N established");
            } else if (frameInput.getType() == 'F'){
                this.closeConnection();
                System.out.println("SENDER go-back-N connection NOT established");
                System.exit(0);
            } else {
                System.out.println("SENDER error in establishing connection");
                System.exit(0);
            }
            //set up time out exception after 3 seconds
            socket.setSoTimeout(TIME_OUT_INTERVAL*1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Main function of the Sender class, this function is called when the thread is started.
     */
    public void run(){
        this.initSenderConnection();

        //create frames
        this.initFrames();
        setupConnection();  // establish connection

        //start to send all the data
        while (true) {

            while (windowIndex <= windowMax && !allFrameSent) { //TODO doit verifier que window est plus grand que nombre de frame

                //check if all frame are sent
                if(windowIndex >= binFrameList.size()){
                    allFrameSent = true;
                    // close connection frame
                    frameToSend = fm.getFrameToSend(new Frame('F', 0));
                } else if (BIT_FLIP & windowIndex==8) {
                    int frame_num = fm.getFramesList().get(windowIndex).getNum();
                    frameToSend = tester.generateBitFlipError(frameToSend, frame_num);
                    BIT_FLIP = false;
                } else {
                    frameToSend = binFrameList.get(windowIndex);
                }

                // send frame
                try {
                    if (windowIndex < binFrameList.size())
                        System.out.println("SENDER (" + (char) fm.getFramesList().get(windowIndex).getType()+ ", "+
                            fm.getFramesList().get(windowIndex).getNum() +", index " + (windowIndex) + ")");
                    out.writeUTF(frameToSend);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (allFrameSent) { // close communication
                    System.out.println("SENDER Sender done");
                    break;
                }

                // update window index
                windowIndex++;

                //for frame lost error, it will skip a frame to simulate a lost frame.
                if(TimeOutError && windowIndex == 10) {
                    windowIndex = tester.simulateFrameLost(windowIndex);
                    TimeOutError = false;
                }
            }

            try {
                input = in.readUTF();
                input = fm.frameExtract(input);
                frameInput = new Frame(input);

                // do an action according to the input
                handleResponse(frameInput);

            } catch (SocketTimeoutException e) {
                System.out.println("SENDER TIMEOUT EXCEPTION");
                handleTimeOut();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //close connection from server received
            if(closeConfirmation) {
                this.closeConnection();
                break;
            }

        }

    }

    public void handleTimeOut() {
        try {
            System.out.println("SENDER sending poll request...");    // On a pas eu d'acquittement du receiver dans les délais on demande donc au receiver à quelle trame il est rendue?
            Frame f = new Frame('P', 0);
            frameToSend = f.getFlag() + DataManipulation.bitStuffing(f.toBin()) + f.getFlag();
            out.writeUTF(frameToSend);
            out.flush();
            poll_req = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleResponse(Frame frameInput) {

        switch (frameInput.getType()){
            case 'A':

                if (poll_req) { // poll request : retransmission des frames à partir du num
                    System.out.println("Retransmission des frames à partir du #" + frameInput.getNum());
                    windowIndex=  nextWindowIndex(windowIndex, frameInput.getNum());
                    windowMin = newWindowMin(windowMin, frameInput.getNum());
                    windowMax = windowMin + (WINDOW_SIZE - 1);
                    poll_req = false;
                } else {
                    //update the window
                    windowMin = newWindowMin(windowMin, frameInput.getNum()); //shift the limit inferior of the window (
                    System.out.println("SENDER windowMin: " + windowMin);

                    windowMax = windowMin + (WINDOW_SIZE - 1);
                    System.out.println("SENDER windowMax: " + windowMax);
                }
                break;
            case 'R':
                windowIndex = previousWindowIndex(windowIndex, frameInput.getNum());  // frames retransmission
                windowMax = windowMin + WINDOW_SIZE-1;
                System.out.println("SENDER Frames retransmission...");
                break;
            case 'F':
                closeConfirmation = true;
//                System.out.println("SENDER confirmation from receiver to close the connection");
                break;
            default:
                System.out.println("SENDER frame contains error");
        }
    }

    /**
     * function that initiate a frame manager to transform all the data that we read into frames. This function also
     * convert the frame into binary and add the flags.
     */
    public void initFrames(){

        byte[][] data = Utils.readLines(fileName);
        fm = new FramesManager();
        fm.createFramesList(data, NUMBER_OF_FRAME);
        binFrameList = fm.getBinFrameList();

    }

    /**
     * initialize the connection with the receiver
     */
    public void initSenderConnection() {
        try {
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("SENDER socket Connected");
    }

    public void closeConnection () {
        try {
            out.close();
            socket.close();
            in.close();
            System.out.println("SENDER sender close connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Find the next windowin after sender received an aknoledgement
     * @param windowMin
     * @param ack
     * @return
     */
    public int newWindowMin (int windowMin, int ack) {

        int indexWindowMin = windowMin%NUMBER_OF_FRAME;

        while (indexWindowMin != ack){
            windowMin++;
            indexWindowMin = (indexWindowMin + 1)%NUMBER_OF_FRAME;
        }

        return windowMin;
    }

    /**
     * Find what is the index to returned to after a REJ.
     * @param windowIndex
     * @param rej
     * @return
     */
    public int previousWindowIndex (int windowIndex, int rej){

        int index = (windowIndex%NUMBER_OF_FRAME) - 1;
        windowIndex--;

        while (index != rej){
            windowIndex--;
            index = (index == 0 ? (NUMBER_OF_FRAME-1) : index - 1)%NUMBER_OF_FRAME;
        }

        return windowIndex;
    }

    private int nextWindowIndex(int windowIndex, int num) {

        int index = (windowIndex%NUMBER_OF_FRAME);

        while (index != num){
            windowIndex--;
            index = (index == 0 ? (NUMBER_OF_FRAME-1) : index)%NUMBER_OF_FRAME;
        }

        return windowIndex;
    }


}
