import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class SenderSimu extends Thread{

    private String address = "";
    private int port;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private int frame_sent;

    public static final int WINDOW_SIZE = 7;    // (2^3) - 1 = 7
    private String[] framesWindow = new String[WINDOW_SIZE];

    //for test, to be deleted ...
    public ArrayList<String> frames = new ArrayList<>();

    /**
     * Constructor
     * @param address address IP of the receiver
     * @param port port to communcate with the receiver
     */
    public SenderSimu(String address, int port){
        this.address = address;
        this.port = port;
    }

    /**
     * initialize the connection with the receiver
     */
    public void initSenderConnection() {
        try {
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            frame_sent = 0;

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connected");
    }

    //For  testing, to be deleted...
    public void initFrames(){

        //simulate frame
        int k;
        for(k = 0; k < 100; k++){
            frames.add(Integer.toString(k));
        }
    }

    /**
     * Main function of the Sender class, this function is called when the thread is started.
     */
    public void run(){
        System.out.println("SENDER thread is running");
        this.initSenderConnection();

        //create fakes frames
        this.initFrames();

        try {
            int windowMin = 0; // inferior limit of the window
            int windowMax = WINDOW_SIZE; //upper limit of the window
            int windowIndex = 0; // at where we are in the list
            int frame_sent = 0;
            boolean done = false;
            String ack;

            while (true) {

                while (windowIndex <= windowMax && !done) {

                    //add number of the frame
                    String frameToSend = frames.get(windowIndex);
                    frameToSend += "-" + windowIndex%WINDOW_SIZE;

                    //sent a frame
                    out.writeUTF(frameToSend);
                    out.flush();
                    System.out.println("SENDER frame sent : " + frameToSend);

                    //update window index
                    windowIndex++;
                }

                //temporary, need to establish another way to signal the end to the receiver, maybe a time out
                if(windowIndex >= frames.size() && !done) {
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

                if(windowIndex == frames.size()){
                    break;
                }
            }

            System.out.println("SENDER sender close connection");
            out.close();
            socket.close();
            in.close();

        } catch (IOException u){
            System.out.println(u);
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
