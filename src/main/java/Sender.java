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

        try {
            int windowMin = 0; // inferior limit of the window
            int windowMax = WINDOW_SIZE; //upper limit of the window
            int windowIndex = 0; // at where we are in the list
            boolean done = false;
            String ack;
            System.out.println("SENDER frame size: " + binFrameList.size());

            while (true) {

                while (windowIndex <= windowMax && !done ) { //TODO doit verifier que windown est plus grand que nombre de frame

                    //add number of the frame
                    String frameToSend = binFrameList.get(windowIndex);

                    //sent a frame
                    out.writeUTF(frameToSend);
                    out.flush();
                    System.out.println("SENDER frame sent : " + frameToSend);

                    //update window index
                    windowIndex++;
                }

                //temporary, need to establish another way to signal the end to the receiver, maybe a time out
                if(windowIndex >= binFrameList.size() && !done) {
                    out.writeUTF("end");
                    done = true;
                    System.out.println("SENDER Sender done");
                }

                //receive from the server
                ack = in.readUTF();
                System.out.println("SENDER ack : " + ack);

                //update the window
                windowMin = newWindowMin(windowMin, Integer.parseInt(ack.substring(ack.length() - 1))); //shit the limit inferior of the window (
                System.out.println("SENDER windowMin: " + windowMin);

                windowMax = windowMin + (WINDOW_SIZE - 1);
                System.out.println("SENDER windowMax: " + windowMax);

                if(windowIndex == binFrameList.size()){
                    break;
                }
            }

            this.closeConnection();
        } catch (IOException u){
            System.out.println(u);
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
        System.out.println("Connected");
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
            indexWindowMin++;
        }

        return windowMin;
    }
}
