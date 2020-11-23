import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Sender extends Thread{

    private String address = "";
    private int port;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private FramesManager fm;
    private String fileName;
    private ArrayList<String> binFrameList;

    int windowMin; // inferior limit of the window
    int windowMax; //upper limit of the window
    int windowIndex; // at where we are in the list
    boolean allFrameSent;
    boolean closeConfirmation;
    String input, ack;
    Frame frameInput;


    public static final int WINDOW_SIZE = 7;    // (2^3) - 1 = 7

    /**
     * Constructor
     * @param address address IP of the receiver
     * @param port port to communcate with the receiver
     */
    public Sender(String address, int port, String fileName){
        this.address = address;
        this.port = port;
        this.fileName = fileName;

        windowMin = 0; // inferior limit of the window
        windowMax = WINDOW_SIZE - 1; //upper limit of the window
        windowIndex = 0; // at where we are in the list
        allFrameSent = false;
        closeConfirmation = false;
    }

    public void setupConnection() throws IOException {

        Frame connectionFrame = new Frame('C', 0);
        out.writeUTF(fm.getFrameToSend(connectionFrame));
        out.flush();
        System.out.println("SENDER connection frame sent");

        //wait for confirmation of the connection
        input = in.readUTF();
        input = fm.handleInput(input);
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

    }

    /**
     * Main function of the Sender class, this function is called when the thread is started.
     */
    public void run(){
        System.out.println("SENDER thread is running");
        this.initSenderConnection();

        //create frames
        this.initFrames();
        System.out.println("SENDER frames ready to be sent");

        System.out.println("SENDER frame size: " + binFrameList.size());

        try {

            setupConnection();  // establish connection

            //start to send all the data
            while (true) {

                while (windowIndex <= windowMax && !allFrameSent ) { //TODO doit verifier que window est plus grand que nombre de frame

                    //add number of the frame
                    String frameToSend = binFrameList.get(windowIndex);

                    //sent a frame
                    out.writeUTF(frameToSend);
                    out.flush();
                    System.out.println("SENDER frame sent : " + frameToSend);

                    //update window index
                    windowIndex++;
                }

                //close the communication
                if(windowIndex >= binFrameList.size() && !allFrameSent) {
                    Frame frameCloseConnection = new Frame('F', 0);
                    out.writeUTF(fm.getFrameToSend(frameCloseConnection));
                    out.flush();
                    allFrameSent = true;
                    System.out.println("SENDER Sender done");
                }

                //receive from the server
                input = in.readUTF();
                input = fm.handleInput(input);
                frameInput = new Frame(input);

                // do an action according to the input
                handleResponse(frameInput);

                //close connection from server received
                if(closeConfirmation) {
                    this.closeConnection();
                    break;
                }

            }

        } catch (IOException u){
            System.out.println(u);
        }

    }

    public void handleResponse(Frame frameInput) {

        System.out.println("SENDER ack : " + frameInput.getNum());
        switch (frameInput.getType()){
            case 'A':
                //TODO
                //update the window
                windowMin = newWindowMin(windowMin, frameInput.getNum()); //shift the limit inferior of the window (
                System.out.println("SENDER windowMin: " + windowMin);

                windowMax = windowMin + (WINDOW_SIZE - 1);
                System.out.println("SENDER windowMax: " + windowMax);
                break;
            case 'F':
                //TODO
                closeConfirmation = true;
                System.out.println("SENDER confirmation from receiver to close the connection");
                break;
            default:
//                        System.out.println("SENDER error in frame");
        }
    }

    /**
     * function that initiate a frame manager to transform all the data that we read into frames. This function also
     * convert the frame into binary and add the flags.
     */
    public void initFrames(){
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
    public void initSenderConnection() {
        try {
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("SENDER socket Connected");
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


    public int newWindowMin (int windowMin, int ack) {

        int indexWindowMin = windowMin%WINDOW_SIZE;

        while (indexWindowMin != ack){
            windowMin++;
            indexWindowMin = (indexWindowMin + 1)%WINDOW_SIZE;
        }

        return windowMin;
    }
}
