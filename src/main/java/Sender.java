import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Sender extends Thread{

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private String address = "";
    private int port;
    public int RR = 3; // number of frames
    private int index = 0;

    public Sender(String address, int port){
        this.address = address;
        this.port = port;
    }

    public void run(){
        System.out.println("thread Sender is running");

        try {

            byte[] data = Files.readAllBytes(Paths.get("src/test/java/test.txt"));
            FramesManager fm = new FramesManager(data);

            ArrayList<Frame> framesList = fm.getFramesList();
            ArrayList<String> binFrames = new ArrayList<>();

            for (Frame f : framesList) {
                binFrames.add(f.getFlag() + fm.bitStuffing(f.toBin()) + f.getFlag());
            }

            socket = new Socket(address, port);
            System.out.println("Connected");

            String frame1 = "1";
            String frame2 = "2";
            String frame3 = "3";
            String frame4 = "4";

            ArrayList<String> frames = new ArrayList<>();
            frames.add(frame1);
            frames.add(frame2);
            frames.add(frame3);
            frames.add(frame4);

            out = new DataOutputStream(socket.getOutputStream());
            

//            for (String binFrame : binFrames) {
//                out.writeUTF(binFrame);
//                out.flush();    // envoi du frame i
//            }

            out.close();
            socket.close();

        } catch (IOException u){
            System.out.println(u);
        }

    }

    public static void main(String args[]){
//        Sender sender = new Sender(args[0], Integer.parseInt(args[1]));
    }
}
