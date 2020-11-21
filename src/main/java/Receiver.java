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
        FramesManager fm = new FramesManager();

        try{
            String input = "";
            int ack;

            //read from sender
            while((!(input = in.readUTF()).equals("end"))){
                System.out.println("RECEIVER frame receive: " + input);
                Frame frame = handleInput(input);

                //ack is the number of the frame + 1
                ack = ((int)frame.getNum()) + 1;

                //send
                out.writeUTF(Integer.toString(ack));
                out.flush();
            }

            this.closeConnection();

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    // TODO : handleInput..

    public Frame handleInput (String input) {
        input = input.substring(8, input.length() - 8);    // without flags
        String binFrame = DataManipulation.bitUnStuffing(input);    // remove bit stuffing

        return new Frame(binFrame);

    }



    public void closeConnection() {
        try{
            in.close();
            out.close();
            socket.close();
            server.close();
            System.out.println("RECEIVER Closing connection");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
//        Receiver receiver = new Receiver(Integer.parseInt(args[0]));
    }
}
