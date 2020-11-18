import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class SenderV2{

    private Socket socket = null;
    public static final int WINDOW_SIZE = 1;    // (2^3) - 1 = 7

    public SenderV2(String address, int port){
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connected");
    }

    public void sendFrames(){

        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("src/test/java/test.txt"));
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

        DataFlowController dfc = new DataFlowController(this.socket);
        dfc.start();

        while (!done) { //frame_sent < WINDOW_SIZE &&
            dfc.send(frames.get(i));
            i++;
            if(i >= frames.size() && !done) {
                dfc.send("end");
                done = true;
            }
        }
//                 TODO si on a recu RR      frame_sent % WINDOW_SIZE

    }

    public static void main(String args[]){
//        Sender sender = new Sender(args[0], Integer.parseInt(args[1]));
    }
}
