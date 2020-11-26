import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;


public class Client {
    private static String address = "";
    private static int port;
    private static Socket socket = null;
    private static int connectionType;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;
    private static FramesManager fm;
    private static String fileName;
    private static ArrayList<String> binFrameList;
    private static Random random;

    static int windowMin; // inferior limit of the window
    static int windowMax; //upper limit of the window
    static int windowIndex; // at where we are in the list
    static boolean allFrameSent;
    static boolean closeConfirmation;
    static boolean closeConnectionFrameSent;
    static String input, ack;
    static Frame frameInput;
    static String frameToSend;

    public static boolean TimeOutError = false;
    public static boolean BIT_FLIP = true;
    public static final int WINDOW_SIZE = 7;    // (2^3) - 1 = 7
    public static final int TIME_OUT_INTERVAL = 3; // 3 seconds time out in go-back-N
    public static boolean poll_req = false;


    /**
     * Constructor
     * @param address address IP of the receiver
     * @param port port to communcate with the receiver
     */
    public Client(String address, int port, String fileName, int connectionType){
        this.address = address;
        this.port = port;
        this.fileName = fileName;
        this.connectionType = connectionType;

        windowMin = 0; // inferior limit of the window
        windowMax = WINDOW_SIZE - 1; //upper limit of the window
        windowIndex = 0; // at where we are in the list
        allFrameSent = false;
        closeConfirmation = false;
        closeConnectionFrameSent = false;

    }

    public static void setupConnection() {

        try {
            Frame connectionFrame = new Frame('C', connectionType);
            out.writeUTF(fm.getFrameToSend(connectionFrame));
            out.flush();
            System.out.println("SENDER connection frame sent");

            //wait for confirmation of the connection
            input = in.readUTF();
            input = fm.frameExtract(input);
            frameInput = new Frame(input);

            if(frameInput.getType() == 'A' && frameInput.getNum() == 0){
                System.out.println("SENDER connection go-back-N established");
            } else if (frameInput.getType() == 'F'){
                closeConnection();
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
    public static void main(String[] args){
        address = "127.0.0.1";
        port = 5000;
        fileName = "src/test/text/test.txt";
        connectionType = 0;
        System.out.println("SENDER thread is running");
        initSenderConnection();

        //create frames
        initFrames();
        System.out.println("SENDER frames ready to be sent");
        System.out.println("SENDER frame size: " + binFrameList.size());

        setupConnection();  // establish connection

        //start to send all the data
        while (true) {

            while (windowIndex <= windowMax && !allFrameSent) { //TODO doit verifier que window est plus grand que nombre de frame

                System.out.println("SENDER windowindex: " + windowIndex);
                //check if all frame are sent
                if(windowIndex >= binFrameList.size()){
                    allFrameSent = true;
                    // close connection frame
                    frameToSend = fm.getFrameToSend(new Frame('F', 0));
                } else {
                    frameToSend = binFrameList.get(windowIndex);
                }

                if (BIT_FLIP & windowIndex==1) { // bit flip error simulation
                    int frame_num = fm.getFramesList().get(windowIndex).getNum();
                    frameToSend = generateBitFlipError(frameToSend, frame_num);
                    BIT_FLIP = false;
                }

                // send frame
                try {
                    out.writeUTF(frameToSend);
                    out.flush();
//                    System.out.println("SENDER frame sent : " + frameToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (allFrameSent) { // close communication
                    System.out.println("SENDER Sender done");
                    break;
                }

                // update window index
                windowIndex++;

                //if transmission lost error is activated   TODO..
//                if(windowIndex == 10 && TimeOutError) {
//                    windowIndex++;
//                    TimeOutError= false; //will happen only one time
//                }
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
                closeConnection();
                break;
            }

        }

    }

    public static void handleTimeOut() {
        try {
            System.out.println("SENDER sending poll request..");
            Frame f = fm.getFrameByType((byte) 'P', 0); // TODO changer num?
            frameToSend = f.getFlag() + DataManipulation.bitStuffing(f.toBin()) + f.getFlag();
            out.writeUTF(frameToSend);
            out.flush();
            poll_req = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void handleResponse(Frame frameInput) {

        switch (frameInput.getType()){
            case 'A':
                System.out.println("SENDER ack : " + frameInput.getNum());

                if (poll_req) { // poll request : retransmission des frames à partir du num
                    System.out.println("Retransmission des frames à partir du #" + frameInput.getNum());
                    windowIndex = windowMin = windowMin + frameInput.getNum();
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
                windowIndex = windowMin;  // frames retransmission TODO : revoir gestion de window ici***
                windowMax = windowMin + WINDOW_SIZE-1;
                System.out.println("Retransmission des frames à partir du #" + frameInput.getNum());
                break;
            case 'F':
                closeConfirmation = true;
                System.out.println("SENDER confirmation from receiver to close the connection");
                break;
            default:
//                        System.out.println("SENDER error in frame");
        }
    }

    public static String generateBitFlipError(String binFrame, int frame_num) {
        random = new Random();
        System.out.println("GÉNÉRATION D'ERREUR (FLIPBIT) au frame #" + frame_num);
        int max_bit_index = binFrame.length()-(8 + 16); // w/o flag and CRC
        int ran_bit_index = random.nextInt(max_bit_index - 8) + 8;  // w/o flag
        return DataManipulation.bitFlip(binFrame, ran_bit_index);   // flip a random bit
    }

    /**
     * function that initiate a frame manager to transform all the data that we read into frames. This function also
     * convert the frame into binary and add the flags.
     */
    public static void initFrames(){
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        fm = new FramesManager();
        fm.createFramesList(data, WINDOW_SIZE);
        binFrameList = fm.getBinFrameList();
    }

    /**
     * initialize the connection with the receiver
     */
    public static void initSenderConnection() {
        try {
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("SENDER socket Connected");
    }

    public static void closeConnection () {
        try {
            out.close();
            socket.close();
            in.close();
            System.out.println("SENDER sender close connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int newWindowMin (int windowMin, int ack) {

        int indexWindowMin = windowMin%WINDOW_SIZE;

        while (indexWindowMin != ack){
            windowMin++;
            indexWindowMin = (indexWindowMin + 1)%WINDOW_SIZE;
        }

        return windowMin;
    }
}

