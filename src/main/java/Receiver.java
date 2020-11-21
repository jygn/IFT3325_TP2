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
            String binary = "";
            int ack;

            //read from sender
            while((!(binary = in.readUTF()).equals("end"))){
                System.out.println("RECEIVER frame receive: " + binary);





                Frame frame = fm.getFrame(binary);

                //evaluate wich type of frame we receive
                switch (frame.getTypeInString()) {
                    case "I": //information
                        //TODO
                        break;
                    case "C": // Connection request
                        //TODO
                        break;
                    case "F": // end of communication
                        //TODO
                        break;
                    case "P": //  P bit
                        //TODO
                        break;
                    default:
                        //TODO throw error??
                }




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
