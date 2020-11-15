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

//            byte data[] = Files.readAllBytes(Paths.get("src/test/java/test.txt"));

            String data = Utility.readFile("src/test/java/test.txt");
            FramesManager fm = new FramesManager(data);
            ArrayList<Frame> framesList = fm.getFramesList();
            // TODO bit stuffing stuffing du header des frames aussi ou non...?
            String t;
            for (Frame f : framesList) {
                t = fm.bitStuffing(f.toBin());
            }


            socket = new Socket(address, port);
            System.out.println("Connected");

            InputStream test = new ByteArrayInputStream("hi I am test".getBytes(StandardCharsets.UTF_8));
            input = new DataInputStream(test);

            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException u){
            System.out.println(u);
        }

        // string to read message from input
        String line = "";

        try
        {
            line = input.readLine();
            out.writeUTF(line);
        }
        catch(IOException i)
        {
            System.out.println("error sender");
        }


        //close connection
        try
        {
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String args[]){
//        Sender sender = new Sender(args[0], Integer.parseInt(args[1]));
    }
}
