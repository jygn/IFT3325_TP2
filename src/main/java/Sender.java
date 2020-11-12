import java.net.*;
import java.io.*;

public class Sender {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public Sender(String address, int port){
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            input = new DataInputStream(System.in);

            out = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u){
            System.out.println(u);
        } catch (IOException i){
            System.out.println(i);
        }

        // string to read message from input
        String line = "";

        // keep reading until "Over" is input
        while (!line.equals("over"))
        {
            try
            {
                line = input.readUTF();
                out.writeUTF(line);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

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
