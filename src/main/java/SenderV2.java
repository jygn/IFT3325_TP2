import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class SenderV2 extends Thread{

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private int frame_sent;

    public static final int WINDOW_SIZE = 1;    // (2^3) - 1 = 7


    public SenderV2(String address, int port){
        try {

            socket = new Socket(address, port);
            //receive
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //send
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            frame_sent = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listener
     */
    public void run () {
        try {
            while (true) {
                String data = in.readUTF();
                frame_sent--;
                System.out.println("ack : " + data);

            }

        } catch (IOException e) {
//            closeConnection();
        }
    }

    public void sendFrames(String filname){

        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get(filname));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FramesManager fm = new FramesManager(data);

        ArrayList<Frame> framesList = fm.getFramesList();
        ArrayList<String> binFrames = new ArrayList<>();

        for (Frame f : framesList) {
            binFrames.add(f.getFlag() + fm.bitStuffing(f.toBin()) + f.getFlag());
        }

        ArrayList<String> frames = new ArrayList<>();

        //simulate frame
        int k;
        for(k = 1; k <= 100; k++){
            frames.add(Integer.toString(k));
        }

        int i = 0;
//            int frame_sent = 0;
        boolean done = false;

        while (!done) { //frame_sent < WINDOW_SIZE &&
            send(frames.get(i));
            i++;
            if(i >= frames.size() && !done) {
                send("end");
                done = true;
            }
        }
//                 TODO si on a recu RR      frame_sent % WINDOW_SIZE

    }


    public void send(String data) {
        try {
            out.writeUTF(data);
            out.flush();
            System.out.println("frame sent : " + data);
            frame_sent++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection () {
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
//        Sender sender = new Sender(args[0], Integer.parseInt(args[1]));
    }
}
