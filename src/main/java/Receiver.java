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

            //receive
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            //send
            PrintStream ps = new PrintStream((socket.getOutputStream()));

            try{

                String frame, ack = "";

                //repeat as long as the client does not send a null string

                //read from sender
                while((!(frame = in.readUTF()).equals("end"))){
                    System.out.println("frame receive: " + frame);
                    ack = frame;

                    //send
                    ps.println(ack);
                }

                System.out.println("Reciver Closing connection");
                socket.close();
                in.close();
                ps.close();
                server.close();

            } catch (IOException e){
                e.printStackTrace();
            }

        } catch (IOException i){
            System.out.println(i);
        }
    }

    public static void main(String args[]){
//        Receiver receiver = new Receiver(Integer.parseInt(args[0]));
    }
}
