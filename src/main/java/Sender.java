import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class Sender extends Thread{

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private String address = "";
    private int port;

    public Sender(String address, int port){
        this.address = address;
        this.port = port;
    }

    public void run(){
        System.out.println("thread Sender is running");

        try {

            String data = Utility.readFile("src/test/java/test.txt");
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


        // string to read message from input
//        String line = "";
//
//        try
//        {
//            line = input.readLine();
//            out.writeUTF(line);
//        }
//        catch(IOException i)
//        {
//            System.out.println("error sender");
//        }

            for (int i = 0; i < binFrames.size(); i++) {
                out.writeUTF(binFrames.get(i));
                out.flush();    // envoi du frame i
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
