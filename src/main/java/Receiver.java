import java.awt.color.ICC_Profile;
import java.net.*;
import java.io.*;

public class Receiver {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;

    public Receiver(int port){

        try{
            server = new ServerSocket(port);
            System.out.println("Server started");

            socket = server.accept();
            System.out.println("Sender accepted");

            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";

            while(!line.equals("over")){
                try{
                    line = in.readUTF();
                    System.out.println(line);
                } catch (IOException i){
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            socket.close();
            in.close();
        } catch (IOException i){
            System.out.println(i);
        }
    }

    public static void main(String args[]){
        Receiver receiver = new Receiver(Integer.parseInt(args[0]));
    }
}
