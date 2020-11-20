import java.net.*;
import java.io.*;

public class ReceiverSimu extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;
    private DataOutputStream out = null;

    public ReceiverSimu(int port){
        this.port = port;
    }

    public void initReceiverConnection(){
        try{
            server = new ServerSocket(port);
            System.out.println("RECEIVER Server started");

            socket = server.accept();
            System.out.println("RECEIVER Sender accepted");

            //receive
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException i){
            System.out.println(i);
        }

    }

    public void run(){
        System.out.println("RECEIVER thread is running");
        this.initReceiverConnection();

        try{
            String frame, ack = "";

            //repeat as long as the client does not send a null string

            //read from sender
            while((!(frame = in.readUTF()).equals("end"))){
                System.out.println("RECEIVER frame receive: " + frame);

                //ack is the number of the frame + 1
                ack = Integer.toString(Integer.parseInt(frame.substring(frame.length() - 1)) + 1);

                //send
                out.writeUTF(ack);
                out.flush();
            }

            System.out.println("RECEIVER Closing connection");
            in.close();
            out.close();
            socket.close();
            server.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String args[]){
//        Receiver receiver = new Receiver(Integer.parseInt(args[0]));
    }
}