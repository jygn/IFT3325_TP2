import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Sender extends Thread{

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private String address = "";
    private int port;


    public static final int WINDOW_SIZE = 1;    // (2^3) - 1 = 7



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


            ArrayList<String> frames = new ArrayList<>();

            //simulate frame
            int k;
            for(k = 1; k <= 100; k++){
                frames.add(Integer.toString(k));
            }

            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            int i = 0;
            int frame_sent = 0;
            boolean done = false;
            String ack;

            while (true) {

                while (frame_sent < WINDOW_SIZE && !done) {
                    out.writeUTF(frames.get(i));
                    out.flush();
                    System.out.println("frame sent : " + frames.get(i));
                    frame_sent++;
                    i++;
                }

                if(i >= frames.size() && !done) {
                    out.writeUTF("end");
                    done = true;
                    System.out.println("Sender done");
                }

                //receive from the server
                ack = input.readUTF();
                frame_sent--;
                System.out.println("ack : " + ack);

                //                ack = br.readLine();

                if(Integer.parseInt(ack) == frames.size()){
                    break;
                }
                // TODO si on a recu RR      frame_sent % WINDOW_SIZE
            }

            System.out.println("sender close connection");
            out.close();
            socket.close();
            input.close();
//            br.close();


        } catch (IOException u){
            System.out.println(u);
        }

    }

    public static void main(String args[]){
//        Sender sender = new Sender(args[0], Integer.parseInt(args[1]));
    }
}
