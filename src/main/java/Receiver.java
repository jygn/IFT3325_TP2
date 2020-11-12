import java.awt.color.ICC_Profile;
import java.net.*;
import java.io.*;

public class Receiver extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;

    public Receiver(int port){
        this.port = port;
    }

    public void run(){
        System.out.println("thread receiver is running");

        try{
            server = new ServerSocket(port);
            System.out.println("Server started");

            socket = server.accept();
            System.out.println("Sender accepted");

            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";

            try{
                line = in.readUTF();
                System.out.println(line);
            } catch (IOException i){
                System.out.println("error receiver");
            }
            System.out.println("Closing connection");

            socket.close();
            in.close();
        } catch (IOException i){
            System.out.println(i);
        }
    }

    public static void main(String args[]){
//        Receiver receiver = new Receiver(Integer.parseInt(args[0]));
    }
}
