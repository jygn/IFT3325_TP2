import java.net.*;
import java.io.*;

public class Receiver extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;
    private DataOutputStream out = null;

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
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            //send
//            PrintStream ps = new PrintStream((socket.getOutputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            try{

                String frame, ack = "";

                //repeat as long as the client does not send a null string

                //read from sender
                while((!(frame = in.readUTF()).equals("end"))){
                    System.out.println("frame receive: " + frame);
                    ack = frame;

                    //send
                    out.writeUTF(ack);
                    out.flush();
                }

                System.out.println("Reciver Closing connection");
                in.close();
//                ps.close();
                out.close();
                socket.close();
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
