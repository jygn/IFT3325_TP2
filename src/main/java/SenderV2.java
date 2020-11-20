import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class SenderV2 extends Thread{

    private String address = "";
    private int port;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private int frame_sent;
    private boolean send_ready;
    private String filename;

    public static final int WINDOW_SIZE = 7;    // (2^3) - 1 = 7


    public SenderV2(String address, int port, String filename){
       this.address = address;
       this.port = port;
       this.filename = filename;
    }

    public void initSender() {
        try {

            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            frame_sent = 0;
            send_ready = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listener
     */
    public void run () {
        this.initSender();

        try {
            while (true) {
                String data = in.readUTF();
//                if (data.length() != 0)
//                    send_ready = true;
                System.out.println("ack : " + data);
                frame_sent--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFrames(){

        // TODO ajouter createFramesList()

        ArrayList<String> frames = new ArrayList<>();

        //simulate frame
        int k;
        for(k = 1; k <= 100; k++){
            frames.add(Integer.toString(k));
        }

        int i = 0;

        while (i < frames.size()) {
            if ((frame_sent < WINDOW_SIZE) & send_ready) {
                send(frames.get(i));
//                send_ready = false;
                i++;
            }
        }
    }


    public void send(String data) {
        try {
            out.writeUTF(data);
            out.flush();
            System.out.println("frame sent : " + data);
            frame_sent++;
            frame_sent = frame_sent%WINDOW_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> createFramesList () {

        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FramesManager fm = new FramesManager();

//        ArrayList<Frame> framesList = fm.getFramesList();
        ArrayList<String> bin_framesList = new ArrayList<>();

//        for (Frame f : framesList) {
//            bin_framesList.add(f.getFlag() + DataManipulation.bitStuffing(f.toBin()) + f.getFlag());
//        }

        return bin_framesList;

    }



}
