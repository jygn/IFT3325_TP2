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


    public static final int WINDOW_SIZE = 7;    // (2^3) - 1 = 7



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

            InputStream test = new ByteArrayInputStream("hi I am test".getBytes(StandardCharsets.UTF_8));
            input = new DataInputStream(test);

            out = new DataOutputStream(socket.getOutputStream());


            int i = 0;
            int frame_sent = 0;

            while (i < binFrames.size()) {

                if (frame_sent < WINDOW_SIZE) {
                    out.writeUTF(binFrames.get(i));
                    out.flush();
                    frame_sent++;
                    i++;
                }

                // TODO si on a recu RR      frame_sent % WINDOW_SIZE
            }

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
